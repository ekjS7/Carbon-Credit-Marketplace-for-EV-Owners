package com.example.demo.controller;

import com.example.demo.entity.Listing;
import com.example.demo.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/listings")
@RequiredArgsConstructor
@Slf4j
public class AdminListingController {

    private final ListingRepository listingRepository;

    //Lấy tất cả listings
    @GetMapping
    public ResponseEntity<List<Listing>> getAllListings() {
        log.info("Admin - Get all listings");
        return ResponseEntity.ok(listingRepository.findAll());
    }

    //Disable 1 listing (chuyển sang trạng thái CANCELLED)
    @PutMapping("/{id}/disable")
    public ResponseEntity<?> disableListing(@PathVariable Long id) {
        log.info("Admin - Disable listing id: {}", id);

        return listingRepository.findById(id)
                .<ResponseEntity<?>>map(listing -> {
                    listing.setStatus(Listing.ListingStatus.CANCELLED);
                    Listing updated = listingRepository.save(listing);

                    return ResponseEntity.ok(Map.of(
                            "message", "Listing " + id + " disabled successfully",
                            "listing", updated
                    ));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Listing not found with ID: " + id));
    }
}
