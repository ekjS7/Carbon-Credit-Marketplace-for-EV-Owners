package com.example.demo.repository;

import com.example.demo.entity.UserStatusRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserStatusRepository extends JpaRepository<UserStatusRecord, Long> {
    Optional<UserStatusRecord> findByUserId(Long userId);
}
