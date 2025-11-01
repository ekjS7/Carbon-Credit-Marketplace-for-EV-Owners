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
import java.util.Map;

@RestController
@RequestMapping("/api/users/disputes")
@RequiredArgsConstructor
@Slf4j
public class UserDisputeController {

    private final DisputeRepository disputeRepository;
    private final TransactionRepository transactionRepository;

    //User mở tranh chấp mới
    @PostMapping("/open")
    public ResponseEntity<?> openDispute(
            @RequestParam Long txId,
            @RequestParam String reason,
            @RequestParam(required = false) String evidenceUrl,
            @RequestParam Long openedByUserId) {

        log.info("User {} mở tranh chấp cho transaction {}", openedByUserId, txId);

        // Kiểm tra lý do có hợp lệ
        if (reason == null || reason.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Reason cannot be empty"
            ));
        }

        // Kiểm tra transaction có tồn tại không
        return transactionRepository.findById(txId)
                .<ResponseEntity<?>>map(tx -> {

                    // Kiểm tra transaction đã có dispute chưa
                    boolean hasActiveDispute = disputeRepository
                            .findAll()
                            .stream()
                            .anyMatch(d -> d.getTransaction().getId().equals(txId)
                                    && d.getStatus() != DisputeStatus.RESOLVED
                                    && d.getStatus() != DisputeStatus.REJECTED);

                    if (hasActiveDispute) {
                        log.warn("Transaction {} already has active dispute", txId);
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                                "error", "Transaction already has an active dispute",
                                "transactionId", txId
                        ));
                    }

                    //Tạo dispute mới
                    Dispute dispute = new Dispute();
                    dispute.setTransaction(tx);
                    dispute.setReason(reason);
                    dispute.setEvidenceUrl(evidenceUrl);
                    dispute.setOpenedByUserId(openedByUserId);
                    dispute.setStatus(DisputeStatus.OPEN);
                    dispute.setCreatedAt(LocalDateTime.now());

                    disputeRepository.save(dispute);

                    log.info("Dispute {} created successfully for transaction {}", dispute.getId(), txId);
                    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                            "message", "Dispute opened successfully",
                            "disputeId", dispute.getId(),
                            "transactionId", txId,
                            "status", dispute.getStatus().name()
                    ));
                })
                .orElseGet(() -> {
                    log.warn("Transaction not found with ID {}", txId);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("error", "Transaction not found with ID " + txId));
                });
    }

    //(Optional) Xem danh sách dispute của user
    @GetMapping
    public ResponseEntity<?> getUserDisputes(@RequestParam Long userId) {
        var disputes = disputeRepository.findAll()
                .stream()
                .filter(d -> d.getOpenedByUserId().equals(userId))
                .toList();

        return ResponseEntity.ok(Map.of(
                "count", disputes.size(),
                "data", disputes
        ));
    }
}
