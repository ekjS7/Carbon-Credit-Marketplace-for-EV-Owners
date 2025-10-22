package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.entity.WalletTransaction;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {
    
    private final UserRepository userRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    
    /**
     * Credit carbon credits to a user's wallet
     * @param userId The user's ID
     * @param amount The amount to credit (must be positive)
     * @param description Transaction description
     * @return The created WalletTransaction
     */
    @Transactional
    public WalletTransaction credit(Long userId, BigDecimal amount, String description) {
        log.info("Crediting {} carbon credits to user ID: {}", amount, userId);
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Credit amount must be positive");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        // Update user balance
        BigDecimal newBalance = user.getCarbonBalance().add(amount);
        user.setCarbonBalance(newBalance);
        userRepository.save(user);
        
        // Create transaction record
        WalletTransaction transaction = new WalletTransaction();
        transaction.setType(WalletTransaction.TransactionType.CREDIT);
        transaction.setAmount(amount);
        
        WalletTransaction savedTransaction = walletTransactionRepository.save(transaction);
        log.info("Credit transaction completed. New balance: {}", newBalance);
        
        return savedTransaction;
    }
    
    /**
     * Debit carbon credits from a user's wallet
     * @param userId The user's ID
     * @param amount The amount to debit (must be positive)
     * @param description Transaction description
     * @return The created WalletTransaction
     */
    @Transactional
    public WalletTransaction debit(Long userId, BigDecimal amount, String description) {
        log.info("Debiting {} carbon credits from user ID: {}", amount, userId);
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Debit amount must be positive");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        // Check sufficient balance
        if (user.getCarbonBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException(
                    String.format("Insufficient balance. Current: %s, Required: %s", 
                            user.getCarbonBalance(), amount));
        }
        
        // Update user balance
        BigDecimal newBalance = user.getCarbonBalance().subtract(amount);
        user.setCarbonBalance(newBalance);
        userRepository.save(user);
        
        // Create transaction record
        WalletTransaction transaction = new WalletTransaction();
        transaction.setType(WalletTransaction.TransactionType.DEBIT);
        transaction.setAmount(amount);
        
        WalletTransaction savedTransaction = walletTransactionRepository.save(transaction);
        log.info("Debit transaction completed. New balance: {}", newBalance);
        
        return savedTransaction;
    }
    
    /**
     * Get the current carbon balance for a user
     * @param userId The user's ID
     * @return The current carbon balance
     */
    public BigDecimal getBalance(Long userId) {
        log.info("Getting balance for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        return user.getCarbonBalance();
    }
}

