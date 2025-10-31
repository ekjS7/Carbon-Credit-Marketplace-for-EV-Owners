package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * OrderTransaction: lưu thông tin 1 giao dịch mua tín chỉ carbon
 * - buyerId, ownerId: user id kiểu Long (theo User entity trong project)
 * - creditsAmount: số tín chỉ (dùng BigDecimal để tránh lỗi số thực)
 * - status: CREATED / IN_TRANSACTION / SUCCESS / FAILED / CANCELLED
 */
@Entity
@Table(name = "order_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long buyerId;

    private Long ownerId;

    /**
     * Số tín chỉ (unit tùy bạn quy ước: kg, tấn, hoặc "credits")
     */
    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal creditsAmount;

    @Column(length = 40, nullable = false)
    private String status; // CREATED, IN_TRANSACTION, SUCCESS, FAILED, CANCELLED

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.status == null) this.status = "CREATED";
    }
}
