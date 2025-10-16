package com.example.demo.repository;

import com.example.demo.entity.EmissionData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

 @Repository
public interface EmissionDataRepository extends JpaRepository<EmissionData, Long> {
    List<EmissionData> findByUserId(Long userId);
} 
