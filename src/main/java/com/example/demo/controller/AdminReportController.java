package com.example.demo.controller;

import com.example.demo.entity.Report;
import com.example.demo.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
@Slf4j
public class AdminReportController {

    private final ReportRepository reportRepository;

    //get all reports
    @GetMapping
    public ResponseEntity<?> getAllReports() {
        log.info("Admin - Get all reports");
        List<Report> reports = reportRepository.findAll();

        if (reports.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(Map.of("message", "No reports found"));
        }

        return ResponseEntity.ok(Map.of(
                "message", "Fetched all reports successfully",
                "total", reports.size(),
                "data", reports
        ));
    }

    //create generate report
    @PostMapping("/generate")
    public ResponseEntity<?> generateReport(@RequestBody Report report) {
        log.info("Admin - Generate new report");

        if (report == null || report.getType() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Report type must not be null"));
        }

        // Gắn thời gian tạo
        report.setCreatedAt(LocalDateTime.now());
        Report saved = reportRepository.save(report);

        log.info("Report generated successfully: id={}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Report generated successfully",
                "report", saved
        ));
    }
}
