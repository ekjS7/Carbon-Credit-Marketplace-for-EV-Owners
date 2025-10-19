package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(nullable = false, columnDefinition = "varchar(255)")
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    
    @Column(nullable = false)
    private Double amount;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "balance_after", nullable = false)
    private Double balanceAfter;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    public enum TransactionType {
        CREDIT,
        DEBIT
    }
}

