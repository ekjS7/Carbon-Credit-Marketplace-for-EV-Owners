package com.example.demo.controller;

import com.example.demo.entity.Listing;
import com.example.demo.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/listings")
@RequiredArgsConstructor
@Slf4j
public class AdminListingController {

    private final ListingRepository listingRepository;

    // ✅ Lấy toàn bộ listing cho admin
    @GetMapping
    public ResponseEntity<?> getAllListings() {
        List<Listing> listings = listingRepository.findAll();

        if (listings.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(Collections.singletonMap("message", "No listings found"));
        }

        // ✅ Dùng HashMap thay cho Map.of() để tương thích mọi version Java
        List<Map<String, Object>> data = listings.stream().map(l -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", l.getId());
            map.put("title", l.getTitle());
            map.put("price", l.getPrice());
            map.put("carbonAmount", l.getCarbonAmount());
            map.put("status", l.getStatus().toString());
            map.put("sellerId", l.getSeller() != null ? l.getSeller().getId() : null);
            return map;
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("total", data.size());
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    // ✅ Disable (vô hiệu hóa) một listing cụ thể
    @PutMapping("/{id}/disable")
    public ResponseEntity<?> disableListing(@PathVariable Long id) {
        return listingRepository.findById(id)
                .<ResponseEntity<?>>map(l -> {
                    l.setStatus(Listing.ListingStatus.CANCELLED);
                    listingRepository.save(l);

                    Map<String, Object> res = new HashMap<>();
                    res.put("message", "Listing disabled successfully");
                    res.put("id", id);

                    return ResponseEntity.ok(res);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "Listing not found")));
    }
}
