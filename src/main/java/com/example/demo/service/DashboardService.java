package com.example.demo.service;

import com.example.demo.dto.DashboardResponse;
import com.example.demo.entity.Listing;
import com.example.demo.entity.ReportStatus;
import com.example.demo.entity.Transaction;
import com.example.demo.repository.ListingRepository;
import com.example.demo.repository.ReportRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final ListingRepository listingRepository;
    private final TransactionRepository transactionRepository;
    private final ReportRepository reportRepository;

    public DashboardResponse getDashboard() {
        long totalUsers = userRepository.count();
        long totalListings = listingRepository.count();
        long totalTransactions = transactionRepository.count();

        long completedTransactions = transactionRepository.findAll().stream()
                .filter(tx -> tx.getStatus() == Transaction.TransactionStatus.COMPLETED)
                .count();

        long cancelledTransactions = transactionRepository.findAll().stream()
                .filter(tx -> tx.getStatus() == Transaction.TransactionStatus.CANCELLED)
                .count();

        // ✅ với Listing: trạng thái "đang hoạt động" = OPEN
        long activeListings = listingRepository.findAll().stream()
                .filter(l -> l.getStatus() == Listing.ListingStatus.OPEN)
                .count();

        // ✅ với Report: trạng thái cần đếm = PENDING
        long reports = reportRepository.findAll().stream()
                .filter(r -> r.getStatus() == ReportStatus.PENDING)
                .count();

        return new DashboardResponse(
                totalUsers,
                totalListings,
                totalTransactions,
                completedTransactions,
                cancelledTransactions,
                activeListings,
                reports
        );
    }
}
