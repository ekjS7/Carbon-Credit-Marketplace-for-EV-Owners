package com.example.demo.service.impl;

import com.example.demo.dto.Co2RequestDto;
import com.example.demo.dto.Co2ResponseDto;
import com.example.demo.entity.Co2Reduction;
import com.example.demo.entity.Co2Status;
import com.example.demo.exception.InvalidEmissionDataException;
import com.example.demo.repository.Co2Repository;
import com.example.demo.service.Co2Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;


@Service
public class Co2ServiceImpl implements Co2Service {

    private static final Logger logger = LoggerFactory.getLogger(Co2ServiceImpl.class);

    // minimal reduction threshold, zero means any positive reduction qualifies
    private static final BigDecimal MIN_REDUCTION = BigDecimal.ZERO;

    private final Co2Repository co2Repository;

    public Co2ServiceImpl(Co2Repository co2Repository) {
        this.co2Repository = co2Repository;
    }

    /**
     * Process emission input:
     * - Validate baseline and actual (units: kg)
     * - Compute reduction = baseline - actual (kg)
     * - Compute credits = reduction (kg) / 1000 -> tonnes (1 tonne = 1 credit)
     * - Use RoundingMode.DOWN to avoid granting more credits than actual reduction.
     */
    @Override
    @Transactional
    public Co2ResponseDto processEmission(Co2RequestDto request) {
        if (request == null) throw new InvalidEmissionDataException("Request is null");

        String userId = request.getUserId();
        BigDecimal baseline = request.getBaseline();
        BigDecimal actual = request.getActual();
        boolean certified = request.isCertified(); // if DTO has this, otherwise false

        if (baseline == null || actual == null) {
            throw new InvalidEmissionDataException("Baseline and actual must be provided");
        }

        // Basic validation: actual should not be greater than baseline in this model
        if (actual.compareTo(baseline) > 0) {
            logger.warn("Phát thải thực tế ({}) lớn hơn cơ sở ({}).", actual, baseline);
            throw new InvalidEmissionDataException("Phát thải thực tế lớn hơn cơ sở.");
        }

        // reduction in kg
        BigDecimal reduction = baseline.subtract(actual);
        if (reduction.compareTo(BigDecimal.ZERO) < 0) {
            reduction = BigDecimal.ZERO;
        }

        // Convert kg to tonnes (1 tonne = 1000 kg), then to credits (1 tCO2 = 1 credit)
        // Use 6 decimal places, rounding down to avoid over-crediting.
        BigDecimal credits = reduction.divide(BigDecimal.valueOf(1000), 6, RoundingMode.DOWN);

        Co2Status status = (reduction.compareTo(MIN_REDUCTION) >= 0 && certified)
                ? Co2Status.APPROVED
                : Co2Status.REJECTED;

        // Persist record
        Co2Reduction record = new Co2Reduction();
        record.setUserId(userId);
        record.setBaseline(baseline);
        record.setActual(actual);
        record.setReduction(reduction);
        record.setCredits(credits);
        record.setCertified(certified);
        record.setStatus(status);
        // createdAt/updatedAt handled by annotations
        co2Repository.save(record);

        // Build response
        Co2ResponseDto response = new Co2ResponseDto();
        response.setRecordId(record.getId());
        response.setBaseline(baseline);
        response.setActual(actual);
        response.setReduction(reduction);
        response.setCredits(credits);
        response.setStatus(status.name());
        response.setMessage("Kết quả: " + status + " (Giảm " + reduction + " kg CO2, tương đương " + credits + " credits)");
        response.setCreatedAt(record.getCreatedAt());
        response.setUpdatedAt(record.getUpdatedAt());

        logger.info("Hoàn tất xử lý CO2 cho user {}: status={}, reduction={} kg, credits={}", userId, status, reduction, credits);

        return response;
    }
}
