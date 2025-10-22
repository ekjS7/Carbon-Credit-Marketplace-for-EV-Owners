package com.example.demo.controller;

import com.example.demo.dto.TransactionRequest;
import com.example.demo.dto.TransactionResponse;
import com.example.demo.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Transaction Management", description = "APIs for managing carbon credit transactions")
public class TransactionController {
    
    private final TransactionService transactionService;
    
    @PostMapping
    @Operation(summary = "Create a new transaction", description = "Create a new transaction to purchase a listing")
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody TransactionRequest request) {
        log.info("Creating new transaction for listing ID: {}", request.getListingId());
        TransactionResponse response = transactionService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/{id}/confirm")
    @Operation(summary = "Confirm transaction", description = "Confirm a pending transaction (seller action)")
    public ResponseEntity<TransactionResponse> confirmTransaction(
            @Parameter(description = "Transaction ID") @PathVariable Long id) {
        log.info("Confirming transaction with ID: {}", id);
        TransactionResponse response = transactionService.confirmTransaction(id);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel transaction", description = "Cancel a pending transaction")
    public ResponseEntity<TransactionResponse> cancelTransaction(
            @Parameter(description = "Transaction ID") @PathVariable Long id) {
        log.info("Cancelling transaction with ID: {}", id);
        TransactionResponse response = transactionService.cancelTransaction(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by ID", description = "Retrieve a specific transaction by its ID")
    public ResponseEntity<TransactionResponse> getTransactionById(
            @Parameter(description = "Transaction ID") @PathVariable Long id) {
        log.info("Fetching transaction with ID: {}", id);
        TransactionResponse transaction = transactionService.getTransactionById(id);
        return ResponseEntity.ok(transaction);
    }
    
    @GetMapping("/mine")
    @Operation(summary = "Get user transactions", description = "Retrieve all transactions for the current user")
    public ResponseEntity<List<TransactionResponse>> getMyTransactions(
            @Parameter(description = "User ID") @RequestParam Long userId) {
        log.info("Fetching transactions for user ID: {}", userId);
        List<TransactionResponse> transactions = transactionService.getTransactionsByUserId(userId);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/buyer/{buyerId}")
    @Operation(summary = "Get transactions by buyer", description = "Retrieve all transactions where user is the buyer")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByBuyer(
            @Parameter(description = "Buyer ID") @PathVariable Long buyerId) {
        log.info("Fetching transactions for buyer ID: {}", buyerId);
        List<TransactionResponse> transactions = transactionService.getTransactionsByBuyer(buyerId);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/seller/{sellerId}")
    @Operation(summary = "Get transactions by seller", description = "Retrieve all transactions where user is the seller")
    public ResponseEntity<List<TransactionResponse>> getTransactionsBySeller(
            @Parameter(description = "Seller ID") @PathVariable Long sellerId) {
        log.info("Fetching transactions for seller ID: {}", sellerId);
        List<TransactionResponse> transactions = transactionService.getTransactionsBySeller(sellerId);
        return ResponseEntity.ok(transactions);
    }
}
