package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "co2_reduction")
public class Co2Reduction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // user identifier (string as in original)
    @Column(name = "user_id", nullable = false, length = 255)
    private String userId;

    // baseline and actual emissions (units: kg CO2)
    @Column(precision = 18, scale = 6, nullable = false)
    private BigDecimal baseline;

    @Column(precision = 18, scale = 6, nullable = false)
    private BigDecimal actual;

    // reduction in kg CO2 (baseline - actual)
    @Column(precision = 18, scale = 6, nullable = false)
    private BigDecimal reduction;

    // NEW: credits = reduction in tonnes (1 tonne CO2 = 1 credit)
    // stored as decimal with 6 fractional digits (adjustable)
    @Column(precision = 18, scale = 6)
    private BigDecimal credits;

    private boolean certified;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Co2Status status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Version
    private int version;
}
