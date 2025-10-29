package com.example.demo.controller;

import com.example.demo.entity.CreditRequest;
import com.example.demo.service.CvaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cva")
public class CvaController {

    private final CvaService cvaService;

    @Autowired
    public CvaController(CvaService cvaService) {
        this.cvaService = cvaService;
    }

    // Lấy danh sách yêu cầu đang chờ duyệt
    @GetMapping("/pending")
    public List<CreditRequest> getPendingRequests() {
        return cvaService.getPendingRequests();
    }

    // Lấy thông tin yêu cầu cụ thể
    @GetMapping("/{id}")
    public CreditRequest getRequestById(@PathVariable Long id) {
        return cvaService.getRequestById(id);
    }

    // Duyệt yêu cầu (APPROVED)
    @PostMapping("/{id}/approve")
    public CreditRequest approveRequest(
            @PathVariable Long id,
            @RequestParam(required = false) String notes
    ) {
        return cvaService.approveRequest(id, notes);
    }

    // Từ chối yêu cầu (REJECTED)
    @PostMapping("/{id}/reject")
    public CreditRequest rejectRequest(
            @PathVariable Long id,
            @RequestParam(required = false) String notes
    ) {
        return cvaService.rejectRequest(id, notes);
    }
}
