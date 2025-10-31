package com.example.demo.controller;

import com.example.demo.entity.CreditRequest;
import com.example.demo.service.CvaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cva")
@CrossOrigin(origins = "*") // Cho phép Postman / Frontend gọi
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

    // CVA duyệt yêu cầu (approve)
    @PutMapping("/approve/{id}")
    public CreditRequest approveRequest(
            @PathVariable Long id,
            @RequestParam(required = false) String notes) {
        return cvaService.approveRequest(id, notes);
    }

    // CVA từ chối yêu cầu (reject)
    @PutMapping("/reject/{id}")
    public CreditRequest rejectRequest(
            @PathVariable Long id,
            @RequestParam(required = false) String notes) {
        return cvaService.rejectRequest(id, notes);
    }
}
