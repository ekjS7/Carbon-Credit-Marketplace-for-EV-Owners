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

    @Column(nullable = false)
    private String userId;

    @Column(precision=18, scale=6)
    private BigDecimal baseline;

    @Column(precision=18, scale=6)
    private BigDecimal actual;

    @Column(precision=18, scale=6)
    private BigDecimal reduction;

    @Column(nullable = false)
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
