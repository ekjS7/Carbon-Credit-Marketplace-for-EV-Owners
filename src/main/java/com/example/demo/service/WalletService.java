package com.example.demo.service;

import com.example.demo.entity.CarbonWallet;
import com.example.demo.entity.User;
import com.example.demo.repository.CarbonWalletRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final UserRepository userRepository;
    private final CarbonWalletRepository carbonWalletRepository;

    /**
     * Helper: lấy ví carbon của user, nếu không có thì báo lỗi rõ ràng.
     */
    private CarbonWallet getCarbonWalletOrThrow(Long userId) {
        return carbonWalletRepository.findByOwner_Id(userId)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy carbon wallet cho user ID: " + userId));
    }

    /**
     * Trả về số dư carbon credit hiện tại của user.
     * Dùng trong TransactionService.createTransaction() để kiểm tra đủ tiền.
     */
    @Transactional(readOnly = true)
    public BigDecimal getBalance(Long userId) {
        CarbonWallet wallet = getCarbonWalletOrThrow(userId);
        return wallet.getBalance();
    }

    /**
     * Cộng tiền/tín chỉ carbon vào ví user.
     * Dùng khi seller nhận tiền sau khi bán.
     */
    @Transactional
    public void credit(Long userId, BigDecimal amount, String description) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Số tiền credit phải > 0");
        }

        // đảm bảo user tồn tại (để message lỗi đẹp hơn)
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy người dùng ID: " + userId));

        CarbonWallet wallet = getCarbonWalletOrThrow(user.getId());

        wallet.setBalance(wallet.getBalance().add(amount));
        carbonWalletRepository.save(wallet);

        // TODO: nếu bạn muốn lưu lịch sử giao dịch sau này
        // bạn có thể tạo CarbonWalletTransaction entity riêng
        // và lưu (userId, amount, type=CREDIT, description)
    }

    /**
     * Trừ tiền/tín chỉ carbon từ ví user.
     * Dùng khi buyer thanh toán.
     */
    @Transactional
    public void debit(Long userId, BigDecimal amount, String description) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Số tiền debit phải > 0");
        }

        // đảm bảo user tồn tại (để message lỗi đẹp hơn)
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy người dùng ID: " + userId));

        CarbonWallet wallet = getCarbonWalletOrThrow(user.getId());

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Số dư không đủ để thực hiện giao dịch!");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        carbonWalletRepository.save(wallet);

        // TODO: ghi log giao dịch nếu cần (giống credit)
    }

    /**
     * Optional tiện ích: chuyển tín chỉ từ A → B.
     * Có thể dùng cho P2P/gifting nếu sau này cần.
     */
    @Transactional
    public void transferCredits(Long fromUserId, Long toUserId, BigDecimal amount) {
        // Trừ ví người gửi
        debit(fromUserId, amount,
                "Chuyển tín chỉ cho user ID " + toUserId);

        // Cộng ví người nhận
        credit(toUserId, amount,
                "Nhận tín chỉ từ user ID " + fromUserId);
    }
}
