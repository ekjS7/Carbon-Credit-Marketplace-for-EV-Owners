package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "carbon_holding")
@Data
public class CarbonHolding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ví tín chỉ mà phần holding này thuộc về
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carbon_wallet_id", nullable = false)
    private CarbonWallet carbonWallet;

    // Loại tín chỉ carbon cụ thể
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_id", nullable = false)
    private CarbonCredit credit;

    // Số lượng tín chỉ user đang giữ cho loại credit này
    @Column(nullable = false)
    private Integer quantity;

    @CreationTimestamp
    @Column(name = "acquired_at", nullable = false, updatable = false)
    private LocalDateTime acquiredAt;
}
