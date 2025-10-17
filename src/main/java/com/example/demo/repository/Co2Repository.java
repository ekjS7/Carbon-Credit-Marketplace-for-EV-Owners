package com.example.demo.repository;

import com.example.demo.entity.Co2Reduction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface Co2Repository extends JpaRepository<Co2Reduction, Long> {
    List<Co2Reduction> findByUserId(String userId);
}
