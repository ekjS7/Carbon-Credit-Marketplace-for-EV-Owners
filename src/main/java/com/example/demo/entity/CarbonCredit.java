package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "carbon_credit")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarbonCredit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ai đang sở hữu lô tín chỉ này (seller / project owner)
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    // Số lượng tín chỉ carbon (ví dụ 12.5000 tCO2e)
    @Column(name = "quantity", precision = 19, scale = 4, nullable = false)
    private BigDecimal quantity;

    // Thời điểm phát hành / cấp tín chỉ
    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    // Đã retire chưa? retire = đã dùng để bù đắp CO2, không thể bán nữa
    @Column(name = "is_retired", nullable = false)
    private boolean retired;
}
