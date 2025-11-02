package com.example.demo.dto;

import com.example.demo.entity.ReportType;

public record CreateReportRequest(
        ReportType type,
        String description,
        String dataPath,
        Long targetUserId,
        Long listingId,
        Long transactionId
) {}
