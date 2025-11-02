package com.example.demo.controller;

import com.example.demo.entity.CarbonCredit;
import com.example.demo.service.CarbonCreditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carbon-credits")
@RequiredArgsConstructor
@Slf4j
public class CarbonCreditController {

    private final CarbonCreditService carbonCreditService;

    /**
     * Lấy tất cả carbon credit mà 1 owner đang nắm giữ (chưa nói gì tới việc có rao bán hay chưa).
     * GET /api/carbon-credits/owner/{ownerId}
     */
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<CarbonCredit>> getCreditsByOwner(@PathVariable Long ownerId) {
        log.info("GET /api/carbon-credits/owner/{}", ownerId);
        List<CarbonCredit> credits = carbonCreditService.getCreditsByOwner(ownerId);
        return ResponseEntity.ok(credits);
    }

    // Các endpoint này tạm thời gỡ để tránh compile error:
    //
    // GET /api/carbon-credits/listed       -> nên chuyển sang ListingController.getOpenListings()
    // POST /api/carbon-credits/{id}/list   -> nên chuyển sang ListingController.createListing(...)
    // POST /api/carbon-credits/{id}/unlist -> nên chuyển sang ListingController.unlistListing(...)
    //
    // Khi bạn thêm ListingController + ListingService thì có thể expose các API marketplace ở đó.
}
