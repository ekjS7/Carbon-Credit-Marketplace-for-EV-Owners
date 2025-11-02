package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
@Slf4j
public class WalletController {

    private final WalletService walletService;
    private final UserRepository userRepository;

    /**
     * Lấy số dư ví tiền của user
     */
    @GetMapping("/{userId}/balance")
    public ResponseEntity<?> getBalance(@PathVariable Long userId) {
        try {
            BigDecimal balance = walletService.getBalance(userId);
            return ResponseEntity.ok(Map.of(
                    "userId", userId,
                    "balance", balance
            ));
        } catch (RuntimeException e) {
            log.error("Error getting balance for user {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Nạp tiền vào ví (hoặc cộng tiền cho seller)
     * dùng cho các giao dịch "top-up" hoặc "seller nhận tiền sau bán hàng"
     */
    @PostMapping("/{userId}/credit")
    public ResponseEntity<?> credit(
            @PathVariable Long userId,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false, defaultValue = "Wallet credited") String description
    ) {
        try {
            log.info("Crediting wallet for user {}: {}", userId, amount);
            walletService.creditFromSale(userId, amount, description);

            BigDecimal newBalance = walletService.getBalance(userId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new BalanceResponse(userId, newBalance, "CREDITED"));
        } catch (Exception e) {
            log.error("Credit failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     *  Trừ tiền khỏi ví (buyer thanh toán)
     */
    @PostMapping("/{userId}/debit")
    public ResponseEntity<?> debit(
            @PathVariable Long userId,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false, defaultValue = "Wallet debited") String description
    ) {
        try {
            log.info("Debiting wallet for user {}: {}", userId, amount);
            walletService.debitForPurchase(userId, amount, description);

            BigDecimal newBalance = walletService.getBalance(userId);
            return ResponseEntity.ok(new BalanceResponse(userId, newBalance, "DEBITED"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * - Rút tiền (về tài khoản ngân hàng)
     * - Gửi request rút
     * - Giảm tiền khỏi ví (Pending)
     */
    @PostMapping("/{userId}/withdraw")
    public ResponseEntity<?> withdraw(
            @PathVariable Long userId,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false, defaultValue = "Withdraw request") String description
    ) {
        try {
            log.info("Withdraw request: user={}, amount={}", userId, amount);
            var tx = walletService.requestWithdraw(userId, amount, description);
            return ResponseEntity.ok(Map.of(
                    "transactionId", tx.getId(),
                    "status", tx.getStatus().name(),
                    "newBalance", tx.getWallet().getBalance()
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DTO phản hồi FE
     */
    public record BalanceResponse(Long userId, BigDecimal balance, String status) {}
}
