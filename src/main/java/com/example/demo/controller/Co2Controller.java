package com.example.demo.controller;

import com.example.demo.entity.EmissionData;
import com.example.demo.service.Co2Service;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/co2")
public class Co2Controller {

    private final Co2Service co2Service;

    public Co2Controller(Co2Service co2Service) {
        this.co2Service = co2Service;
    }

   
    @PostMapping("/check")
    public String checkOneTon(@RequestBody List<EmissionData> records) {
        boolean reached = co2Service.hasReachedOneTon(records);
        return reached
                ? "Đã đạt đủ 1 tấn CO₂ giảm phát thải!"
                : "Chưa đủ 1 tấn CO₂, tiếp tục đóng góp!";
    }

    
    @PostMapping("/total")
    public String totalEmission(@RequestBody List<EmissionData> records) {
        double totalKg = co2Service.totalAvoidedEmission(records);
        return String.format("Tổng lượng giảm phát thải: %.2f kg CO₂", totalKg);
    }

    @GetMapping("/calculate")
public String calculateCO2(@RequestParam double distanceKm) {
    double co2Emission = distanceKm * 0.12; // ví dụ 1 km = 0.12 kg CO2
    return String.format("Lượng CO₂ giảm phát thải: %.2f kg", co2Emission);
}
}


