package com.example.demo.controller;

import com.example.demo.entity.Transaction;
import com.example.demo.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin/transactions")
@RequiredArgsConstructor
@Slf4j
public class AdminTransactionController {

    private final TransactionRepository transactionRepository;

    @GetMapping
    public ResponseEntity<?> getAllTransactions() {
        log.info("Admin - Get all transactions");

        List<Transaction> transactions = transactionRepository.findAll();

        if (transactions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(Map.of("message", "No transactions found"));
        }

        // ✅ Chuyển sang DTO thủ công (loại bỏ proxy)
        List<Map<String, Object>> data = new ArrayList<>();

        for (Transaction tx : transactions) {
            Map<String, Object> dto = new LinkedHashMap<>();

            dto.put("id", tx.getId());
            dto.put("listingId", safeGetId(() -> tx.getListing().getId()));
            dto.put("buyerId", safeGetId(() -> tx.getBuyer().getId()));
            dto.put("sellerId", safeGetId(() -> tx.getSeller().getId()));
            dto.put("amount", tx.getAmount());
            dto.put("status", tx.getStatus() != null ? tx.getStatus().toString() : null);
            dto.put("createdAt", tx.getCreatedAt());

            data.add(dto);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Fetched all transactions successfully");
        response.put("total", data.size());
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    // 🧩 Hàm helper tránh lỗi LazyInitialization
    private Object safeGetId(Supplier<Object> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return null;
        }
    }

    @FunctionalInterface
    private interface Supplier<T> {
        T get();
    }
}
