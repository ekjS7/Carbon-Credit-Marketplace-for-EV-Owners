package com.example.demo.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Co2ResponseDto {
    private Long recordId;
    private BigDecimal baseline;   // kg
    private BigDecimal actual;     // kg
    private BigDecimal reduction;  // kg
    private BigDecimal credits;    // tonnes -> credits
    private String status;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
