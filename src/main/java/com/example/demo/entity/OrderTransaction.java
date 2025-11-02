package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * OrderTransaction = "đơn giao dịch tín chỉ carbon" ở mức marketplace.
 *
 * - buyerId: người mua
 * - ownerId: người bán (người đang sở hữu tín chỉ trước giao dịch)
 * - creditId: loại tín chỉ carbon nào đang giao dịch
 * - quantity: số lượng tín chỉ carbon chuyển giao
 * - creditsAmount: tổng số tiền (VND) tương ứng giao dịch (có thể dùng để audit)
 *
 * status:
 *   IN_TRANSACTION  -> buyer đã yêu cầu mua, đang chờ confirm
 *   SUCCESS         -> đã chuyển tín chỉ thành công
 *   FAILED          -> giao dịch lỗi
 *   CANCELLED       -> buyer/seller hủy trước khi chuyển tín chỉ
 */
@Entity
@Table(name = "order_transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ai mua
    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;

    // ai bán / chủ sở hữu tín chỉ
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    // loại tín chỉ carbon nào (tham chiếu CarbonCredit.id)
    @Column(name = "credit_id", nullable = false)
    private Long creditId;

    // số lượng tín chỉ chuyển giao
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    // tổng tiền của giao dịch (có thể null nếu tạm thời chưa dùng)
    @Column(name = "credits_amount", precision = 19, scale = 2)
    private BigDecimal creditsAmount;

    // trạng thái giao dịch
    @Column(name = "status", length = 32, nullable = false)
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
