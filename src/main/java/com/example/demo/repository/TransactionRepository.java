package com.example.demo.repository;

import com.example.demo.entity.Transaction;
import com.example.demo.entity.Transaction.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByBuyerIdOrderByCreatedAtDesc(Long buyerId);
    
    List<Transaction> findBySellerIdOrderByCreatedAtDesc(Long sellerId);
    
    List<Transaction> findByStatusOrderByCreatedAtDesc(TransactionStatus status);
    
    @Query("SELECT t FROM Transaction t WHERE t.buyer.id = :userId OR t.seller.id = :userId ORDER BY t.createdAt DESC")
    List<Transaction> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    List<Transaction> findByListingIdOrderByCreatedAtDesc(Long listingId);
    
    @Query("SELECT t FROM Transaction t WHERE t.listing.id = :listingId AND t.status = :status")
    List<Transaction> findByListingIdAndStatus(@Param("listingId") Long listingId, @Param("status") TransactionStatus status);
}
