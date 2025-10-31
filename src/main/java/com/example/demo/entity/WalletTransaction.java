package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type; // CREDIT / DEBIT

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Liên kết với ví
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Column(length = 255)
    private String description;

    public enum TransactionType {
        CREDIT, DEBIT
    }
}
