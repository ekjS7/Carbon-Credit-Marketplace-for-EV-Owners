package com.example.demo.dto;

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
    private Double baseline;

    @NotNull
    @PositiveOrZero
    private Double actual;

    private boolean certified;

    @NotBlank
    private String userId;
}

