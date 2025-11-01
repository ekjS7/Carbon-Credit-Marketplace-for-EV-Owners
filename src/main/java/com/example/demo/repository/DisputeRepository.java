package com.example.demo.repository;

import com.example.demo.entity.Dispute;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DisputeRepository extends JpaRepository<Dispute, Long> {
    List<Dispute> findByOpenedByUserId(Long userId);
    boolean existsByTransactionId(Long transactionId); // thêm dòng này
}
