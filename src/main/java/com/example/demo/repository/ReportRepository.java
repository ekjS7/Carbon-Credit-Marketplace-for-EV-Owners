package com.example.demo.repository;

import com.example.demo.entity.Report;
import com.example.demo.entity.ReportStatus;
import com.example.demo.entity.ReportType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Page<Report> findByStatus(ReportStatus status, Pageable pageable);
    Page<Report> findByType(ReportType type, Pageable pageable);
    Page<Report> findByTypeAndStatus(ReportType type, ReportStatus status, Pageable pageable);
}
