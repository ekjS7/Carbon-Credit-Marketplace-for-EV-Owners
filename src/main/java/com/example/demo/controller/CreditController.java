package com.example.demo.controller;

import com.example.demo.entity.CreditRequest;
import com.example.demo.service.CreditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/credit")
@RequiredArgsConstructor
@Slf4j
public class CreditController {

    private final CreditService creditService;

    // user gửi yêu cầu rút tiền / duyệt thủ công
    @PostMapping("/request")
    public ResponseEntity<?> submitRequest(@RequestBody CreditRequest req) {
        CreditRequest saved = creditService.submitRequest(req);
        return ResponseEntity.ok(saved);
    }

    // admin duyệt
    @PostMapping("/{requestId}/approve")
    public ResponseEntity<?> approve(@PathVariable Long requestId) {
        CreditRequest updated = creditService.approveRequest(requestId);
        return ResponseEntity.ok(updated);
    }

    // admin từ chối
    @PostMapping("/{requestId}/reject")
    public ResponseEntity<?> reject(
            @PathVariable Long requestId,
            @RequestParam String reason
    ) {
        CreditRequest updated = creditService.rejectRequest(requestId, reason);
        return ResponseEntity.ok(updated);
    }
}
