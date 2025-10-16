package com.example.demo.service;

import com.example.demo.entity.EmissionData;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class Co2Service {

    // Lượng CO2 trung bình xe xăng thải ra (g/km)
    private static final double GASOLINE_CAR_EMISSION = 192.0;

    // Tính lượng CO2 tránh phát thải (kg)
    public double calculateAvoidedEmission(EmissionData data) {
        double evEmission = data.getDistanceKm() * data.getEfficiency() * data.getEmissionFactor();
        double gasEmission = data.getDistanceKm() * GASOLINE_CAR_EMISSION / 1000; // g → kg
        return gasEmission - evEmission;
    }

    // Kiểm tra đã đạt 1 tấn CO₂ giảm phát thải chưa
    public boolean hasReachedOneTon(List<EmissionData> records) {
        double total = records.stream().mapToDouble(this::calculateAvoidedEmission).sum();
        return total >= 1000.0;
    }

    // Tính tổng lượng giảm phát thải
    public double totalAvoidedEmission(List<EmissionData> records) {
        return records.stream().mapToDouble(this::calculateAvoidedEmission).sum();
    }
}
