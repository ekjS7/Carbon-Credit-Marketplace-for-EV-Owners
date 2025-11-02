package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    // Loại giao dịch: nạp, mua, bán, rút
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TransactionType type;

    // Trạng thái giao dịch: chờ, thành công, thất bại
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionStatus status = TransactionStatus.PENDING;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    // Ví tiền liên kết
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    // Mã đơn dùng để đối soát với VNPAY hoặc hệ thống ngoài
    @Column(name = "order_ref", unique = true, length = 100)
    private String orderRef;

    // Kênh thanh toán: VNPAY / INTERNAL / BANK_TRANSFER
    @Column(name = "payment_gateway", length = 50)
    private String paymentGateway;

    // Mô tả chi tiết giao dịch
    @Column(length = 255)
    private String description;

    // Lý do lỗi (nếu thất bại)
    @Column(name = "fail_reason", length = 255)
    private String failReason;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    public enum TransactionType {
        TOPUP,          // Nạp tiền
        PURCHASE,       // Mua tín chỉ
        SALE_PROCEEDS,  // Doanh thu bán tín chỉ
        WITHDRAW        // Rút tiền
    }

    public enum TransactionStatus {
        PENDING,        // Đang chờ xử lý / xác nhận
        SUCCESS,        // Thành công
        FAILED          // Thất bại
    }
}
