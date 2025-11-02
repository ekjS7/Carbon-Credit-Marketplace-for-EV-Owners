package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * CreditRequest = yêu cầu mà user gửi lên hệ thống.
 *
 * Hiện tại dùng cho 2 luồng:
 *  1. RÚT / NẠP TIỀN ví:    amount = số tiền VND
 *  2. YÊU CẦU TÍN CHỈ CARBON:
 *     - ownerId        = chủ dự án / seller
 *     - carbonAmount   = số lượng tín chỉ carbon (ví dụ tấn CO2e)
 *     - notes          = ghi chú (ví dụ: bên thẩm định CVA cập nhật)
 *
 * Admin / hệ thống sẽ chuyển trạng thái:
 *   PENDING / APPROVED / REJECTED
 */
@Entity
@Table(name = "credit_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ai gửi request (user thực hiện hành động)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // --- BLOCK cho luồng ví tiền ---
    // Số tiền VND mà user muốn rút / nạp thủ công
    @Column(name = "amount", precision = 19, scale = 2)
    private BigDecimal amount;

    // --- BLOCK cho luồng phát hành tín chỉ carbon ---
    // Chủ sở hữu tín chỉ / chủ dự án (có thể trùng hoặc khác userId)
    @Column(name = "owner_id")
    private Long ownerId;

    // Số lượng tín chỉ carbon đề nghị (ví dụ: tCO2e)
    @Column(name = "carbon_amount", precision = 19, scale = 4)
    private BigDecimal carbonAmount;

    // Ghi chú nội bộ từ đội thẩm định / CVA / admin
    @Column(name = "notes", length = 1000)
    private String notes;

    // --- TRẠNG THÁI DUYỆT ---
    // PENDING / APPROVED / REJECTED
    @Column(name = "status", length = 32, nullable = false)
    private String status;

    // Lý do từ chối nếu REJECTED
    @Column(name = "reject_reason", length = 255)
    private String rejectReason;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
