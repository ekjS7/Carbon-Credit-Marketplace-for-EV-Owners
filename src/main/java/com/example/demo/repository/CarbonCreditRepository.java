package com.example.demo.repository;

import com.example.demo.entity.CarbonCredit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CarbonCreditRepository extends JpaRepository<CarbonCredit, Long> {

    // tất cả credit thuộc về 1 owner cụ thể
    List<CarbonCredit> findByOwnerId(Long ownerId);
}
