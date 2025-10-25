package com.example.demo.repository;

import com.example.demo.entity.OrderTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderTransactionRepository extends JpaRepository<OrderTransaction, Long> {
    // có thể thêm findByBuyerId / findByOwnerId nếu cần
}
