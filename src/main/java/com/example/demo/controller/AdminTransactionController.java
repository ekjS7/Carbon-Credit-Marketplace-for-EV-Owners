package com.example.demo.controller;

import com.example.demo.entity.Transaction;
import com.example.demo.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/transactions")
@RequiredArgsConstructor
@Slf4j
public class AdminTransactionController {

    private final TransactionRepository transactionRepository;

    // Lấy tất cả transactions
    @GetMapping
    public ResponseEntity<?> getAllTransactions() {
        log.info("Admin - Get all transactions");
        List<Transaction> transactions = transactionRepository.findAll();

        if (transactions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(Map.of("message", "No transactions found"));
        }

        return ResponseEntity.ok(Map.of(
                "message", "Fetched all transactions successfully",
                "total", transactions.size(),
                "data", transactions
        ));
    }

    //Hủy (cancel) giao dịch
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelTransaction(@PathVariable Long id) {
        log.info("Admin - Cancel transaction id: {}", id);

        return transactionRepository.findById(id)
                .<ResponseEntity<?>>map(tx -> {
                    if (tx.getStatus() == Transaction.TransactionStatus.CANCELLED) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("error", "Transaction is already cancelled"));
                    }

                    tx.setStatus(Transaction.TransactionStatus.CANCELLED);
                    transactionRepository.save(tx);

                    log.info("Transaction {} cancelled successfully", id);
                    return ResponseEntity.ok(Map.of(
                            "message", "Transaction cancelled successfully",
                            "transaction", tx
                    ));
                })
                .orElseGet(() -> {
                    log.warn("Transaction not found with ID: {}", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("error", "Transaction not found with ID: " + id));
                });
    }
}
