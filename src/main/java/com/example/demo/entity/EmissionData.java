package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmissionData {
    private double distanceKm;      // Quãng đường (km)
    private double efficiency;      // Hiệu suất xe điện (kWh/km)
    private double emissionFactor;  // Hệ số phát thải CO2 (kg/kWh)
}
