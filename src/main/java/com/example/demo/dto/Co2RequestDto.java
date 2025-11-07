package com.example.demo.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Co2RequestDto {

    @NotNull
    @PositiveOrZero
    private BigDecimal baseline;

    @NotNull
    @PositiveOrZero
    private BigDecimal actual;

    private boolean certified;

    @NotBlank
    private String userId;
}

