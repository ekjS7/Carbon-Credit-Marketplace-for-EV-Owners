package com.example.demo.service;

import com.example.demo.entity.CarbonCredit;
import com.example.demo.repository.CarbonCreditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarbonCreditService {

    private final CarbonCreditRepository carbonCreditRepository;

    /**
     * Lấy toàn bộ carbon credit mà 1 owner (project owner / seller) đang sở hữu.
     */
    public List<CarbonCredit> getCreditsByOwner(Long ownerId) {
        log.info("Fetching carbon credits for owner {}", ownerId);
        return carbonCreditRepository.findByOwnerId(ownerId);
    }

    // NOTE:
    // Các hành vi như:
    // - đưa credit lên marketplace
    // - gỡ credit khỏi marketplace
    // - xem danh sách credit đang mở bán
    //
    // => nên được xử lý ở ListingService, không phải ở đây.
}
