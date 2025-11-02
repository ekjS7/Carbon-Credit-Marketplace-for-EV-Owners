package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.entity.Wallet;
import com.example.demo.entity.WalletTransaction;
import com.example.demo.entity.WalletTransaction.TransactionType;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WalletRepository;
import com.example.demo.repository.WalletTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class WalletService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletTransactionRepository transactionRepository;

    /**
     * EXISTING helper method names (kept for internal usage)
     * creditWallet, debitWallet, transferCredits
     * (You might have them already — we keep behavior the same.)
     */

    /** New method expected by other classes: getBalance(userId) */
    @Transactional(readOnly = true)
    public BigDecimal getBalance(Long userId) {
        Wallet wallet = walletRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ví của người dùng ID: " + userId));
        return wallet.getBalance();
    }

    /** New method expected by other classes: credit(userId, amount, description) */
    @Transactional
    public WalletTransaction credit(Long userId, BigDecimal amount, String description) {
        // delegate to existing credit logic (or implement directly)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng ID: " + userId));

        Wallet wallet = user.getWallet();
        if (wallet == null) {
            throw new RuntimeException("Người dùng chưa có ví!");
        }

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        WalletTransaction transaction = new WalletTransaction();
        transaction.setWallet(wallet);
        transaction.setAmount(amount);
        transaction.setType(TransactionType.CREDIT);
        transaction.setDescription(description);
        transactionRepository.save(transaction);

        return transaction;
    }

    /** New method expected by other classes: debit(userId, amount, description) */
    @Transactional
    public WalletTransaction debit(Long userId, BigDecimal amount, String description) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng ID: " + userId));

        Wallet wallet = user.getWallet();
        if (wallet == null) {
            throw new RuntimeException("Người dùng chưa có ví!");
        }

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Số dư không đủ để thực hiện giao dịch!");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        WalletTransaction transaction = new WalletTransaction();
        transaction.setWallet(wallet);
        transaction.setAmount(amount);
        transaction.setType(TransactionType.DEBIT);
        transaction.setDescription(description);
        transactionRepository.save(transaction);

        return transaction;
    }

    /**
     * If you still want named helpers creditWallet/debitWallet/transferCredits,
     * keep them or add wrappers to call the above methods.
     */
    @Transactional
    public void transferCredits(Long ownerId, Long buyerId, BigDecimal amount) {
        debit(ownerId, amount, "Trừ tín chỉ khi bán cho Buyer ID " + buyerId);
        credit(buyerId, amount, "Nhận tín chỉ từ Owner ID " + ownerId);
    }
}
