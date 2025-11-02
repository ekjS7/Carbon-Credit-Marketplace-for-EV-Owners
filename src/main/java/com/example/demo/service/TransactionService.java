package com.example.demo.service;

import com.example.demo.dto.TransactionRequest;
import com.example.demo.dto.TransactionResponse;
import com.example.demo.entity.Listing;
import com.example.demo.entity.Transaction;
import com.example.demo.entity.User;
import com.example.demo.repository.ListingRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final WalletService walletService;

    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {
        log.info("Creating transaction for listing {} by buyer {}", request.getListingId(), request.getBuyerId());

        Listing listing = listingRepository.findById(request.getListingId())
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        if (listing.getStatus() != Listing.ListingStatus.OPEN) {
            throw new RuntimeException("Listing not available");
        }

        User buyer = userRepository.findById(request.getBuyerId())
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        if (buyer.getId().equals(listing.getSeller().getId())) {
            throw new RuntimeException("Buyer cannot purchase their own listing");
        }

        BigDecimal buyerBalance = walletService.getBalance(buyer.getId());
        BigDecimal price = listing.getPrice();

        if (buyerBalance.compareTo(price) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        Transaction tx = new Transaction();
        tx.setBuyer(buyer);
        tx.setSeller(listing.getSeller());
        tx.setListing(listing);
        tx.setAmount(price);
        tx.setStatus(Transaction.TransactionStatus.PENDING);
        tx = transactionRepository.save(tx);

        listing.setStatus(Listing.ListingStatus.RESERVED);
        listingRepository.save(listing);

        return TransactionResponse.fromTransaction(tx);
    }

    @Transactional
    public TransactionResponse confirmTransaction(Long transactionId) {
        log.info("Confirming transaction {}", transactionId);

        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (tx.getStatus() != Transaction.TransactionStatus.PENDING)
            throw new RuntimeException("Transaction not in PENDING state");

        // Trừ tiền buyer
        walletService.debitForPurchase(
                tx.getBuyer().getId(),
                tx.getAmount(),
                "Purchase of listing: " + tx.getListing().getTitle()
        );

        // Cộng tiền seller
        walletService.creditFromSale(
                tx.getSeller().getId(),
                tx.getAmount(),
                "Sale of listing: " + tx.getListing().getTitle()
        );

        tx.setStatus(Transaction.TransactionStatus.COMPLETED);
        transactionRepository.save(tx);

        tx.getListing().setStatus(Listing.ListingStatus.SOLD);
        listingRepository.save(tx.getListing());

        return TransactionResponse.fromTransaction(tx);
    }

    @Transactional
    public TransactionResponse cancelTransaction(Long id) {
        log.info("Cancelling transaction {}", id);
        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        if (tx.getStatus() != Transaction.TransactionStatus.PENDING)
            throw new RuntimeException("Only pending can be cancelled");

        tx.setStatus(Transaction.TransactionStatus.CANCELLED);
        transactionRepository.save(tx);

        tx.getListing().setStatus(Listing.ListingStatus.OPEN);
        listingRepository.save(tx.getListing());

        return TransactionResponse.fromTransaction(tx);
    }

    public List<TransactionResponse> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(TransactionResponse::fromTransaction).collect(Collectors.toList());
    }

    public List<TransactionResponse> getTransactionsByBuyer(Long buyerId) {
        return transactionRepository.findByBuyerIdOrderByCreatedAtDesc(buyerId)
                .stream().map(TransactionResponse::fromTransaction).collect(Collectors.toList());
    }

    public List<TransactionResponse> getTransactionsBySeller(Long sellerId) {
        return transactionRepository.findBySellerIdOrderByCreatedAtDesc(sellerId)
                .stream().map(TransactionResponse::fromTransaction).collect(Collectors.toList());
    }

    public TransactionResponse getTransactionById(Long id) {
        return TransactionResponse.fromTransaction(
                transactionRepository.findById(id).orElseThrow(() -> new RuntimeException("Transaction not found"))
        );
    }
}
