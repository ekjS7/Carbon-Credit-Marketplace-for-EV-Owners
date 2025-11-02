package com.example.demo.dto;

import com.example.demo.entity.ReportStatus;

public record UpdateReportStatusRequest(
        ReportStatus status,
        String resolutionNote
) {}
