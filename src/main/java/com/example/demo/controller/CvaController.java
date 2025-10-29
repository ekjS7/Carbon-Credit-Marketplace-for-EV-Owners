package com.example.demo.controller;

import com.example.demo.entity.CreditRequest;
import com.example.demo.service.CvaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cva")
@RequiredArgsConstructor
public class CvaController {

    private final CvaService cvaService;

    @GetMapping("/requests/pending")
    public ResponseEntity<List<CreditRequest>> getPendingRequests() {
        return ResponseEntity.ok(cvaService.getPendingRequests());
    }

    @GetMapping("/requests/{id}")
    public ResponseEntity<CreditRequest> getRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(cvaService.getRequestById(id));
    }

    @PostMapping("/requests/{id}/approve")
    public ResponseEntity<String> approveRequest(@PathVariable Long id, @RequestParam Long verifierId) {
        cvaService.approveRequest(id, verifierId);
        return ResponseEntity.ok("Yêu cầu đã được phê duyệt thành công.");
    }

    @PostMapping("/requests/{id}/reject")
    public ResponseEntity<String> rejectRequest(@PathVariable Long id, @RequestParam Long verifierId, @RequestParam String reason) {
        cvaService.rejectRequest(id, verifierId, reason);
        return ResponseEntity.ok("Yêu cầu đã bị từ chối: " + reason);
    }
}
