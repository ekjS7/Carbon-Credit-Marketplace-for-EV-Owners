package com.example.demo.controller;

import com.example.demo.entity.Report;
import com.example.demo.service.ReportService;
import com.example.demo.dto.CreateReportRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService service;
    public ReportController(ReportService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<Report> submit(@RequestParam Long reporterId,
                                         @RequestBody CreateReportRequest req) {
        return ResponseEntity.ok(service.submitReport(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Report> detail(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }
}
