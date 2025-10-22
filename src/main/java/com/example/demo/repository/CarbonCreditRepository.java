package com.example.demo.repository;

import com.example.demo.entity.CarbonCredit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarbonCreditRepository extends JpaRepository<CarbonCredit, Long> {
    List<CarbonCredit> findByOwnerId(Long ownerId);
    List<CarbonCredit> findByListedTrue();
}
