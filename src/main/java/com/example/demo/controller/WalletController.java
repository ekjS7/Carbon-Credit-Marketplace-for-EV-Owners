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
     * Tạo ví ban đầu cho user (chỉ dùng lúc bootstrap / admin).
     * Lưu ý: logic hiện tại tạo kiểu "wallet thường".
     * Nếu bạn đã chuyển hoàn toàn sang carbon_wallet và không dùng entity Wallet nữa
     * thì endpoint này có thể bị deprecate. Giữ tạm theo code gốc nhưng tối giản.
     */
    @PostMapping
    public ResponseEntity<?> createWallet(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.parseLong(request.get("userId").toString());
            BigDecimal balance = new BigDecimal(request.get("balance").toString());

            // Tìm user
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Nếu bạn còn trường user.getWallet() (ví cũ) thì giữ check này.
            // Nếu sau này bạn bỏ hẳn entity Wallet truyền thống thì có thể xoá cả block createWallet này.
            if (user.getWallet() != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Wallet already exists for this user"));
            }

            // Nếu bạn vẫn còn entity Wallet và muốn tạo nó ở đây thì bạn sẽ cần:
            // - new Wallet()
            // - setUser(user)
            // - setBalance(balance)
            // - user.setWallet(wallet)
            // - userRepository.save(user)
            //
            // Tuy nhiên vì ta đã migrate sang CarbonWallet, controller này
            // thực tế không nên tạo ví kiểu cũ nữa.
            //
            // Mình sẽ trả về 410 Gone để nhắc bạn migrate endpoint này sang CarbonWalletService.createWallet(...)
            log.warn("createWallet() was called but legacy Wallet model is being phased out.");
            return ResponseEntity.status(HttpStatus.GONE)
                    .body(Map.of(
                            "warning", "Legacy wallet creation endpoint is deprecated. Use carbon wallet initialization instead.",
                            "userId", userId,
                            "requestedInitialBalance", balance
                    ));

        } catch (Exception e) {
            log.error("Failed to create wallet: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "error", "Failed to create wallet",
                            "message", e.getMessage()
                    ));
        }
    }

    /**
     * Lấy số dư carbon hiện tại của user (đọc từ carbon_wallet).
     */
    @GetMapping("/{userId}/balance")
    public ResponseEntity<?> getBalance(@PathVariable Long userId) {
        try {
            log.info("Getting balance for user ID: {}", userId);

            BigDecimal balance = walletService.getBalance(userId);

            return ResponseEntity.ok(
                    Map.of(
                            "userId", userId,
                            "carbonBalance", balance
                    )
            );
        } catch (RuntimeException e) {
            log.error("Error getting balance for user ID {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Cộng carbon credit vào ví của user (nạp tiền / seller nhận tiền).
     */
    @PostMapping("/{userId}/credit")
    public ResponseEntity<?> credit(
            @PathVariable Long userId,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false, defaultValue = "Carbon credit added") String description
    ) {
        try {
            log.info("Credit request for user ID {}: amount={}", userId, amount);

            walletService.credit(userId, amount, description);

            BigDecimal newBalance = walletService.getBalance(userId);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new BalanceResponse(userId, newBalance, "CREDITED"));

        } catch (IllegalArgumentException e) {
            log.error("Invalid credit request for user ID {}: {}", userId, e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));

        } catch (RuntimeException e) {
            log.error("Credit failed for user ID {}: {}", userId, e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Trừ carbon credit khỏi ví của user (buyer thanh toán).
     */
    @PostMapping("/{userId}/debit")
    public ResponseEntity<?> debit(
            @PathVariable Long userId,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false, defaultValue = "Carbon credit deducted") String description
    ) {
        try {
            log.info("Debit request for user ID {}: amount={}", userId, amount);

            walletService.debit(userId, amount, description);

            BigDecimal newBalance = walletService.getBalance(userId);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new BalanceResponse(userId, newBalance, "DEBITED"));

        } catch (IllegalArgumentException e) {
            log.error("Invalid debit request for user ID {}: {}", userId, e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));

        } catch (RuntimeException e) {
            log.error("Error processing debit for user ID {}: {}", userId, e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DTO trả về sau mỗi lần nạp/rút để FE biết userId, số dư mới và trạng thái.
     */
    public static class BalanceResponse {
        private final Long userId;
        private final BigDecimal balance;
        private final String status;

        public BalanceResponse(Long userId, BigDecimal balance, String status) {
            this.userId = userId;
            this.balance = balance;
            this.status = status;
        }

        public Long getUserId() {
            return userId;
        }

        public BigDecimal getBalance() {
            return balance;
        }

        public String getStatus() {
            return status;
        }
    }
}
