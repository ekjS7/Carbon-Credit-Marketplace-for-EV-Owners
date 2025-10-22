package com.example.demo.controller;

import com.example.demo.dto.ListingRequest;
import com.example.demo.dto.ListingResponse;
import com.example.demo.service.ListingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Listing Management", description = "APIs for managing carbon credit listings")
public class ListingController {
    
    private final ListingService listingService;
    
    @PostMapping
    @Operation(summary = "Create a new listing", description = "Create a new carbon credit listing")
    public ResponseEntity<ListingResponse> createListing(@Valid @RequestBody ListingRequest request) {
        log.info("Creating new listing: {}", request.getTitle());
        ListingResponse response = listingService.createListing(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    @Operation(summary = "Get all listings", description = "Retrieve all listings with pagination")
    public ResponseEntity<Page<ListingResponse>> getAllListings(Pageable pageable) {
        log.info("Fetching all listings");
        Page<ListingResponse> listings = listingService.getAllListings(pageable);
        return ResponseEntity.ok(listings);
    }
    
    @GetMapping("/open")
    @Operation(summary = "Get open listings", description = "Retrieve all open listings with pagination")
    public ResponseEntity<Page<ListingResponse>> getOpenListings(Pageable pageable) {
        log.info("Fetching open listings");
        Page<ListingResponse> listings = listingService.getOpenListings(pageable);
        return ResponseEntity.ok(listings);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search listings", description = "Search listings by keyword")
    public ResponseEntity<Page<ListingResponse>> searchListings(
            @Parameter(description = "Search keyword") @RequestParam String keyword,
            Pageable pageable) {
        log.info("Searching listings with keyword: {}", keyword);
        Page<ListingResponse> listings = listingService.searchListings(keyword, pageable);
        return ResponseEntity.ok(listings);
    }
    
    @GetMapping("/seller/{sellerId}")
    @Operation(summary = "Get listings by seller", description = "Retrieve all listings created by a specific seller")
    public ResponseEntity<List<ListingResponse>> getListingsBySeller(
            @Parameter(description = "Seller ID") @PathVariable Long sellerId) {
        log.info("Fetching listings for seller ID: {}", sellerId);
        List<ListingResponse> listings = listingService.getListingsBySeller(sellerId);
        return ResponseEntity.ok(listings);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get listing by ID", description = "Retrieve a specific listing by its ID")
    public ResponseEntity<ListingResponse> getListingById(
            @Parameter(description = "Listing ID") @PathVariable Long id) {
        log.info("Fetching listing with ID: {}", id);
        ListingResponse listing = listingService.getListingById(id);
        return ResponseEntity.ok(listing);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update listing", description = "Update an existing listing")
    public ResponseEntity<ListingResponse> updateListing(
            @Parameter(description = "Listing ID") @PathVariable Long id,
            @Valid @RequestBody ListingRequest request) {
        log.info("Updating listing with ID: {}", id);
        ListingResponse response = listingService.updateListing(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete listing", description = "Delete an existing listing")
    public ResponseEntity<Void> deleteListing(
            @Parameter(description = "Listing ID") @PathVariable Long id) {
        log.info("Deleting listing with ID: {}", id);
        listingService.deleteListing(id);
        return ResponseEntity.noContent().build();
    }
}
