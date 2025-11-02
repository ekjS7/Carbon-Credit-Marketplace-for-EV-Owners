package com.example.demo.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class UserUpdateRequest {
    private String email;
    private String fullName;
    private BigDecimal carbonBalance;
    private String role;
}
