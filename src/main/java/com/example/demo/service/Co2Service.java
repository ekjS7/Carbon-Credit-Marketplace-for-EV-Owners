package com.example.demo.service;

import com.example.demo.entity.Co2Reduction;
import com.example.demo.repository.Co2Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Co2Service {

    @Autowired
    private Co2Repository co2Repository;

    public String processEmission(double baseline, double actual, boolean certified, String userId) {
        double reduction = baseline - actual;
        if (baseline < actual) {
            return "Dữ liệu không hợp lệ: phát thải thực tế lớn hơn cơ sở.";
        }

        String status;
        if (reduction < 1000) {
            status = "REJECTED";
        } else if (!certified) {
            status = "REJECTED";
        } else {
            status = "APPROVED";
        }

        Co2Reduction record = new Co2Reduction();
        record.setUserId(userId);
        record.setBaseline(baseline);
        record.setActual(actual);
        record.setReduction(reduction);
        record.setCertified(certified);
        record.setStatus(status);
        co2Repository.save(record);

        return "Kết quả: " + status + " (Giảm " + reduction + " kg CO2)";
    }
}
