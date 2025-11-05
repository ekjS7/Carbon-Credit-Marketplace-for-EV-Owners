package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "carbon_credits")
@Data
public class CarbonCredit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ownerId;   // userId từ bảng User

    @Column(nullable = false)
    private Double amount;  // số lượng tín chỉ được phát hành

    private String source;  // nguồn gốc (vd: Request#id)

    @CreationTimestamp
    private LocalDateTime issuedAt;

    @Column(nullable = false)
    private boolean listed = false; // có niêm yết trên marketplace không?
}
