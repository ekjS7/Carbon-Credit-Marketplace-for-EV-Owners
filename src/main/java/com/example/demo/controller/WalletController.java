package com.example.demo.controller;

import com.example.demo.entity.WalletTransaction;
import com.example.demo.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
@Slf4j
public class WalletController {
    
    private final WalletService walletService;
    
    /**
     * Get current carbon balance for a user
     * @param userId The user's ID
     * @return Current carbon balance
     */
    @GetMapping("/{userId}/balance")
    public ResponseEntity<?> getBalance(@PathVariable Long userId) {
        try {
            log.info("Getting balance for user ID: {}", userId);
            BigDecimal balance = walletService.getBalance(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("carbonBalance", balance);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error getting balance for user ID {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Credit carbon credits to a user's wallet
     * @param userId The user's ID
     * @param amount The amount to credit
     * @param description Optional transaction description
     * @return The created transaction
     */
    @PostMapping("/{userId}/credit")
    public ResponseEntity<?> credit(
            @PathVariable Long userId,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false, defaultValue = "Carbon credit added") String description) {
        try {
            log.info("Credit request for user ID {}: amount={}", userId, amount);
            WalletTransaction transaction = walletService.credit(userId, amount, description);
            return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
        } catch (IllegalArgumentException e) {
            log.error("Invalid credit request for user ID {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            log.error("Error processing credit for user ID {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Debit carbon credits from a user's wallet
     * @param userId The user's ID
     * @param amount The amount to debit
     * @param description Optional transaction description
     * @return The created transaction
     */
    @PostMapping("/{userId}/debit")
    public ResponseEntity<?> debit(
            @PathVariable Long userId,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false, defaultValue = "Carbon credit deducted") String description) {
        try {
            log.info("Debit request for user ID {}: amount={}", userId, amount);
            WalletTransaction transaction = walletService.debit(userId, amount, description);
            return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
        } catch (IllegalArgumentException e) {
            log.error("Invalid debit request for user ID {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            log.error("Error processing debit for user ID {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}

