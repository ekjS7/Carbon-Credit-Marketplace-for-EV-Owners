package com.example.demo.controller;

import com.example.demo.entity.CreditRequest;
import com.example.demo.service.CreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/credit-requests")
@RequiredArgsConstructor
public class CreditController {

    private final CreditService creditService;

    @PostMapping
    public ResponseEntity<CreditRequest> submitRequest(@RequestBody CreditRequest request) {
        return ResponseEntity.ok(creditService.submitRequest(request));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<CreditRequest> approve(@PathVariable Long id) {
        return ResponseEntity.ok(creditService.approveRequest(id));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<CreditRequest> reject(@PathVariable Long id,
                                                @RequestParam String reason) {
        return ResponseEntity.ok(creditService.rejectRequest(id, reason));
    }
}
