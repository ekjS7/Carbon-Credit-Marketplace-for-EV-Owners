package com.example.demo.controller;

import com.example.demo.entity.Dispute;
import com.example.demo.entity.DisputeStatus;
import com.example.demo.repository.DisputeRepository;
import com.example.demo.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/disputes")
@RequiredArgsConstructor
@Slf4j
public class UserDisputeController {

    private final DisputeRepository disputeRepository;
    private final TransactionRepository transactionRepository;

    // 🟢 User mở tranh chấp
    @PostMapping("/open")
    public ResponseEntity<?> openDispute(@RequestParam Long txId,
                                         @RequestParam String reason,
                                         @RequestParam(required = false) String evidenceUrl,
                                         @RequestParam Long openedByUserId) {
        log.info("User {} mở tranh chấp cho transaction {}", openedByUserId, txId);

        // Kiểm tra giao dịch có tồn tại không
        return transactionRepository.findById(txId)
                .<ResponseEntity<?>>map(tx -> {
                    // Kiểm tra nếu giao dịch đã có tranh chấp trước đó
                    boolean exists = disputeRepository.existsByTransactionId(txId);
                    if (exists) {
                        return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(Map.of(
                                        "error", "Transaction already has an active dispute",
                                        "transactionId", txId
                                ));
                    }

                    // Tạo mới dispute
                    Dispute dispute = new Dispute();
                    dispute.setTransaction(tx);
                    dispute.setReason(reason);
                    dispute.setEvidenceUrl(evidenceUrl);
                    dispute.setOpenedByUserId(openedByUserId);
                    dispute.setOpenedAt(LocalDateTime.now());
                    dispute.setStatus(DisputeStatus.PENDING);

                    disputeRepository.save(dispute);

                    log.info("Dispute {} created successfully for transaction {}", dispute.getId(), txId);
                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body(Map.of(
                                    "message", "Dispute opened successfully",
                                    "disputeId", dispute.getId(),
                                    "transactionId", txId,
                                    "status", "PENDING"
                            ));
                })
                .orElseGet(() -> {
                    log.warn("Transaction not found with ID {}", txId);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("error", "Transaction not found with ID " + txId));
                });
    }

    // 🟡 (Optional) User xem lại danh sách tranh chấp của chính mình
    @GetMapping("/my/{userId}")
    public ResponseEntity<?> getMyDisputes(@PathVariable Long userId) {
        log.info("User {} yêu cầu xem danh sách tranh chấp của mình", userId);
        List<Dispute> disputes = disputeRepository.findByOpenedByUserId(userId);

        if (disputes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(Map.of("message", "No disputes found for user " + userId));
        }

        return ResponseEntity.ok(Map.of(
                "total", disputes.size(),
                "data", disputes
        ));
    }

    // 🟡 (Optional) Xem chi tiết 1 tranh chấp cụ thể (nếu cần hiển thị chi tiết trên giao diện)
    @GetMapping("/{id}")
    public ResponseEntity<?> getDisputeById(@PathVariable Long id) {
        log.info("User xem chi tiết tranh chấp {}", id);
        return disputeRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Dispute not found with ID " + id)));
    }
}
