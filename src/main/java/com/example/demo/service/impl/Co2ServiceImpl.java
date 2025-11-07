package com.example.demo.service.impl;

import com.example.demo.dto.Co2RequestDto;
import com.example.demo.dto.Co2ResponseDto;
import com.example.demo.entity.Co2Reduction;
import com.example.demo.entity.Co2Status;
import com.example.demo.exception.InvalidEmissionDataException;
import com.example.demo.repository.Co2Repository;
import com.example.demo.service.Co2Service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class Co2ServiceImpl implements Co2Service {

    private static final Logger logger = LoggerFactory.getLogger(Co2ServiceImpl.class);
    private final Co2Repository co2Repository;
    
    private static final BigDecimal MIN_REDUCTION = new BigDecimal("1000.0");

    public Co2ServiceImpl(Co2Repository co2Repository) {
        this.co2Repository = co2Repository;
    }

    @Override
    @Transactional
    public Co2ResponseDto processEmission(Co2RequestDto request) {
        BigDecimal baseline = request.getBaseline();
        BigDecimal actual = request.getActual();
        boolean certified = request.isCertified();
        String userId = request.getUserId();

        logger.info("Bắt đầu xử lý CO2 cho user {}", userId);

        if (baseline.compareTo(actual) < 0) {
            logger.warn("Phát thải thực tế ({}) lớn hơn cơ sở ({}).", actual, baseline);
            throw new InvalidEmissionDataException("Phát thải thực tế lớn hơn cơ sở.");
        }

        BigDecimal reduction = baseline.subtract(actual);

        Co2Status status = (reduction.compareTo(MIN_REDUCTION) >= 0 && certified)
                ? Co2Status.APPROVED
                : Co2Status.REJECTED;

        Co2Reduction record = new Co2Reduction();
        record.setUserId(userId);
        record.setBaseline(baseline);
        record.setActual(actual);
        record.setReduction(reduction);
        record.setCertified(certified);
        record.setStatus(status);
        co2Repository.save(record);

        Co2ResponseDto response = new Co2ResponseDto();
        response.setRecordId(record.getId());
        response.setReduction(reduction);
        response.setStatus(status.name());
        response.setMessage("Kết quả: " + status + " (Giảm " + reduction + " kg CO2)");

        logger.info("Hoàn tất xử lý CO2 cho user {}: status={}, reduction={}", userId, status, reduction);

        return response;
    }
}