package com.example.demo.controller;

import com.example.demo.entity.Listing;
import com.example.demo.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin/listings")
@RequiredArgsConstructor
@Slf4j
public class AdminListingController {

    private final ListingRepository listingRepository;

    @GetMapping
    public ResponseEntity<?> getAllListings() {
        List<Listing> listings = listingRepository.findAll();
        if (listings.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(Map.of("message", "No listings found"));

        List<Map<String, Object>> data = listings.stream().map(l -> Map.of(
                "id", l.getId(),
                "price", l.getPrice(),
                "quantity", l.getQuantity(),
                "status", l.getStatus().toString(),
                "sellerId", l.getSeller() != null ? l.getSeller().getId() : null
        )).toList();

        return ResponseEntity.ok(Map.of("total", data.size(), "data", data));
    }

    @PutMapping("/{id}/disable")
    public ResponseEntity<?> disableListing(@PathVariable Long id) {
        return listingRepository.findById(id)
                .<ResponseEntity<?>>map(l -> {
                    l.setStatus(Listing.ListingStatus.CANCELLED);
                    listingRepository.save(l);
                    return ResponseEntity.ok(Map.of("message", "Listing disabled", "id", id));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Listing not found")));
    }
}
