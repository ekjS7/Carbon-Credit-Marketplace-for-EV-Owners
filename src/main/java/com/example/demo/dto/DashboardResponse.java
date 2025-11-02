package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponse {
    private long totalUsers;
    private long totalListings;
    private long totalTransactions;
    private long completedTransactions;
    private long cancelledTransactions;
    private long activeListings;
    private long reports;
}
