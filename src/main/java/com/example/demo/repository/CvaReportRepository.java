package com.example.demo.repository;

import com.example.demo.entity.CvaReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CvaReportRepository extends JpaRepository<CvaReport, Long> {
}
