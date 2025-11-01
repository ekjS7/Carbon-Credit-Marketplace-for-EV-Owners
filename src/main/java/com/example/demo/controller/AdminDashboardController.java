package com.example.demo.controller;

import com.example.demo.entity.Transaction;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.ListingRepository;
import com.example.demo.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardController {

    private final UserRepository userRepository;
    private final ListingRepository listingRepository;
    private final TransactionRepository transactionRepository;

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        log.info("Admin - Get dashboard summary");

        Map<String, Object> data = new HashMap<>();
        data.put("totalUsers", userRepository.count());
        data.put("totalListings", listingRepository.count());
        data.put("totalTransactions", transactionRepository.count());

        // 🧮 Lấy toàn bộ giao dịch 1 lần duy nhất
        List<Transaction> transactions = transactionRepository.findAll();

        long completed = transactions.stream()
                .filter(t -> t.getStatus() == Transaction.TransactionStatus.COMPLETED)
                .count();
        long cancelled = transactions.stream()
                .filter(t -> t.getStatus() == Transaction.TransactionStatus.CANCELLED)
                .count();
        long pending = transactions.stream()
                .filter(t -> t.getStatus() == Transaction.TransactionStatus.PENDING)
                .count();
        long confirmed = transactions.stream()
                .filter(t -> t.getStatus() == Transaction.TransactionStatus.CONFIRMED)
                .count();

        // ✅ Gộp kết quả vào map
        data.put("transactionsCompleted", completed);
        data.put("transactionsCancelled", cancelled);
        data.put("transactionsPending", pending);
        data.put("transactionsConfirmed", confirmed);

        return ResponseEntity.ok(data);
    }
}
