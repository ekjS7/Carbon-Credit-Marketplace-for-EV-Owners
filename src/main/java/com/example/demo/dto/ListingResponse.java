package com.example.demo.dto;

import com.example.demo.entity.Listing;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListingResponse {
    
    private Long id;
    private String title;
    private String description;
    private BigDecimal carbonAmount;
    private BigDecimal price;
    private Listing.ListingStatus status;
    private LocalDateTime createdAt;
    private Long sellerId;
    private String sellerName;
    private String sellerEmail;
    
    public static ListingResponse fromListing(Listing listing) {
        ListingResponse response = new ListingResponse();
        response.setId(listing.getId());
        response.setTitle(listing.getTitle());
        response.setDescription(listing.getDescription());
        response.setCarbonAmount(listing.getCarbonAmount());
        response.setPrice(listing.getPrice());
        response.setStatus(listing.getStatus());
        response.setCreatedAt(listing.getCreatedAt());
        response.setSellerId(listing.getSeller().getId());
        response.setSellerName(listing.getSeller().getFullName());
        response.setSellerEmail(listing.getSeller().getEmail());
        return response;
    }
}
