package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.entity.Wallet;
import com.example.demo.entity.WalletTransaction;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WalletRepository;
import com.example.demo.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
@Slf4j
public class WalletController {

    private final WalletService walletService;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    /**
     * ✅ Create a wallet for a user
     */
    @PostMapping
    public ResponseEntity<?> createWallet(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.parseLong(request.get("userId").toString());
            BigDecimal balance = new BigDecimal(request.get("balance").toString());

            // 🔹 Lấy user có thật trong DB
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 🔹 Nếu user đã có ví thì không tạo lại
            if (user.getWallet() != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Wallet already exists for this user"));
            }

            // 🔹 Tạo ví mới, gán quan hệ 2 chiều
            Wallet wallet = new Wallet();
            wallet.setBalance(balance);
            wallet.setUser(user);
            user.setWallet(wallet);

            // Quan trọng: lưu user thay vì wallet
            userRepository.save(user);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Wallet created successfully",
                            "userId", userId,
                            "balance", balance
                    ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "error", "Failed to create wallet",
                            "message", e.getMessage()
                    ));
        }
    }

    /**
     * Get current carbon balance for a user
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
