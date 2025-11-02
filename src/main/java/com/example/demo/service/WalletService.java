package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.entity.Wallet;
import com.example.demo.entity.WalletTransaction;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WalletRepository;
import com.example.demo.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final UserRepository userRepository;

    /**
     * Lấy ví tiền (Wallet) của user. Nếu chưa có thì tạo mới với balance = 0.
     */
    @Transactional
    public Wallet getOrCreateWallet(Long userId) {
        return walletRepository.findByUser_Id(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

                    Wallet w = new Wallet();
                    w.setUser(user);
                    w.setBalance(BigDecimal.ZERO);
                    return walletRepository.save(w);
                });
    }

    /**
     * Lấy số dư ví tiền của user.
     */
    @Transactional(readOnly = true)
    public BigDecimal getBalance(Long userId) {
        Wallet wallet = walletRepository.findByUser_Id(userId)
                .orElseThrow(() -> new NoSuchElementException("Wallet not found for user " + userId));
        return wallet.getBalance();
    }

    /**
     * Ghi nhận giao dịch (WalletTransaction) - dùng nội bộ trong service.
     */
    private WalletTransaction logTransaction(
            Wallet wallet,
            WalletTransaction.TransactionType type,
            WalletTransaction.TransactionStatus status,
            BigDecimal amount,
            String description,
            String orderRef,
            String gateway,
            String failReason
    ) {
        WalletTransaction tx = new WalletTransaction();
        tx.setWallet(wallet);
        tx.setType(type);
        tx.setStatus(status);
        tx.setAmount(amount);
        tx.setDescription(description);
        tx.setOrderRef(orderRef);
        tx.setPaymentGateway(gateway);
        tx.setFailReason(failReason);
        return walletTransactionRepository.save(tx);
    }

    /**
     * TOPUP thành công (sau callback VNPAY):
     * - cộng tiền vào ví user
     * - đánh dấu transaction SUCCESS
     */
    @Transactional
    public void confirmTopUpSuccess(String orderRef) {
        WalletTransaction tx = walletTransactionRepository.findByOrderRef(orderRef)
                .orElseThrow(() -> new NoSuchElementException("Transaction not found: " + orderRef));

        // Chỉ xử lý nếu đang ở trạng thái PENDING để tránh cộng tiền 2 lần
        if (tx.getStatus() != WalletTransaction.TransactionStatus.PENDING) {
            return;
        }

        Wallet wallet = tx.getWallet();

        // Cộng tiền
        wallet.setBalance(wallet.getBalance().add(tx.getAmount()));
        walletRepository.save(wallet);

        // Cập nhật transaction
        tx.setStatus(WalletTransaction.TransactionStatus.SUCCESS);
        tx.setFailReason(null);
        walletTransactionRepository.save(tx);
    }

    /**
     * TOPUP thất bại (callback VNPAY báo lỗi)
     */
    @Transactional
    public void markTopUpFailed(String orderRef, String failReason) {
        WalletTransaction tx = walletTransactionRepository.findByOrderRef(orderRef)
                .orElseThrow(() -> new NoSuchElementException("Transaction not found: " + orderRef));

        if (tx.getStatus() != WalletTransaction.TransactionStatus.PENDING) {
            return;
        }

        tx.setStatus(WalletTransaction.TransactionStatus.FAILED);
        tx.setFailReason(failReason);
        walletTransactionRepository.save(tx);
    }

    /**
     * Khởi tạo yêu cầu nạp tiền (trước khi redirect VNPAY)
     * -> tạo transaction PENDING
     * -> trả về orderRef để build URL VNPAY
     */
    @Transactional
    public WalletTransaction initTopUp(Long userId, BigDecimal amount, String gateway) {
        Wallet wallet = getOrCreateWallet(userId);

        // tạo mã orderRef duy nhất gửi VNPAY
        String orderRef = "TOPUP_" + userId + "_" + System.currentTimeMillis();

        WalletTransaction tx = new WalletTransaction();
        tx.setWallet(wallet);
        tx.setType(WalletTransaction.TransactionType.TOPUP);
        tx.setStatus(WalletTransaction.TransactionStatus.PENDING);
        tx.setAmount(amount);
        tx.setDescription("Top-up via " + gateway);
        tx.setOrderRef(orderRef);
        tx.setPaymentGateway(gateway);
        tx.setFailReason(null);

        return walletTransactionRepository.save(tx);
    }

    /**
     * Trừ tiền ví khi mua tín chỉ (buyer trả tiền)
     * - ném lỗi nếu không đủ số dư
     */
    @Transactional
    public void debitForPurchase(Long buyerId, BigDecimal totalAmount, String desc) {
        Wallet buyerWallet = walletRepository.findByUser_Id(buyerId)
                .orElseThrow(() -> new NoSuchElementException("Wallet not found for buyer " + buyerId));

        if (buyerWallet.getBalance().compareTo(totalAmount) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }

        // Trừ tiền
        buyerWallet.setBalance(buyerWallet.getBalance().subtract(totalAmount));
        walletRepository.save(buyerWallet);

        // Ghi lịch sử PURCHASE
        logTransaction(
                buyerWallet,
                WalletTransaction.TransactionType.PURCHASE,
                WalletTransaction.TransactionStatus.SUCCESS,
                totalAmount,
                desc,
                null,               // orderRef không bắt buộc cho giao dịch nội bộ
                "INTERNAL",
                null
        );
    }

    /**
     * Cộng tiền ví cho seller khi bán tín chỉ (seller nhận tiền)
     */
    @Transactional
    public void creditFromSale(Long sellerId, BigDecimal totalAmount, String desc) {
        Wallet sellerWallet = getOrCreateWallet(sellerId);

        // Cộng tiền
        sellerWallet.setBalance(sellerWallet.getBalance().add(totalAmount));
        walletRepository.save(sellerWallet);

        // Ghi lịch sử SALE_PROCEEDS
        logTransaction(
                sellerWallet,
                WalletTransaction.TransactionType.SALE_PROCEEDS,
                WalletTransaction.TransactionStatus.SUCCESS,
                totalAmount,
                desc,
                null,
                "INTERNAL",
                null
        );
    }

    /**
     * Request rút tiền (WITHDRAW).
     * Ở đây mình sẽ trừ tiền khỏi ví ngay và tạo transaction ở trạng thái PENDING,
     * để chờ admin/chuyển khoản thủ công duyệt.
     */
    @Transactional
    public WalletTransaction requestWithdraw(Long userId, BigDecimal amount, String desc) {
        Wallet wallet = walletRepository.findByUser_Id(userId)
                .orElseThrow(() -> new NoSuchElementException("Wallet not found for user " + userId));

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance for withdraw");
        }

        // Trừ trước để "giữ chỗ"
        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        WalletTransaction tx = new WalletTransaction();
        tx.setWallet(wallet);
        tx.setType(WalletTransaction.TransactionType.WITHDRAW);
        tx.setStatus(WalletTransaction.TransactionStatus.PENDING); // chờ xử lý payout
        tx.setAmount(amount);
        tx.setDescription(desc);
        tx.setPaymentGateway("BANK_TRANSFER");
        tx.setOrderRef("WITHDRAW_" + userId + "_" + System.currentTimeMillis());
        tx.setFailReason(null);

        return walletTransactionRepository.save(tx);
    }

}
