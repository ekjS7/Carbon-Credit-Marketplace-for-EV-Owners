package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Transaction;
import com.example.demo.repository.ListingRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardController {

    private final UserRepository userRepository;
    private final ListingRepository listingRepository;
    private final TransactionRepository transactionRepository;
        private final com.example.demo.service.DashboardSseService dashboardSseService;

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        log.info("Admin - Get dashboard summary");

        Map<String, Object> data = new HashMap<>();
        data.put("totalUsers", userRepository.count());
        data.put("totalListings", listingRepository.count());
        data.put("totalTransactions", transactionRepository.count());

        // Lấy toàn bộ giao dịch 1 lần duy nhất
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

        // Gộp kết quả vào map
        data.put("transactionsCompleted", completed);
        data.put("transactionsCancelled", cancelled);
        data.put("transactionsPending", pending);
        data.put("transactionsConfirmed", confirmed);

        return ResponseEntity.ok(data);
    }

        @GetMapping("/stream")
        public org.springframework.web.servlet.mvc.method.annotation.SseEmitter stream() {
                log.info("Admin - Open dashboard SSE stream");
                return dashboardSseService.registerEmitter();
        }

        @PostMapping("/refresh")
        public ResponseEntity<Void> refreshNow() {
                log.info("Admin - Manual refresh requested");
                dashboardSseService.manualPublish();
                return ResponseEntity.ok().build();
        }

        @GetMapping("/clients")
        public ResponseEntity<Integer> clients() {
                return ResponseEntity.ok(dashboardSseService.getConnectedClientCount());
        }

        @PostMapping("/refresh/interval")
        public ResponseEntity<Void> setInterval(@RequestParam long seconds) {
                dashboardSseService.setRefreshInterval(seconds);
                return ResponseEntity.ok().build();
        }

        @PostMapping("/refresh/toggle")
        public ResponseEntity<Void> toggle(@RequestParam boolean enabled) {
                dashboardSseService.setRefreshing(enabled);
                return ResponseEntity.ok().build();
        }
}
