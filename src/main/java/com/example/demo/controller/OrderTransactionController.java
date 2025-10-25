package com.example.demo.controller;

import com.example.demo.entity.OrderTransaction;
import com.example.demo.service.OrderTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderTransactionController {

    private final OrderTransactionService orderService;

    /**
     * Tạo order (Buyer ấn mua)
     * POST /api/orders/create?buyerId=1&ownerId=2&credits=10.5
     */
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestParam Long buyerId,
                                    @RequestParam Long ownerId,
                                    @RequestParam BigDecimal credits) {
        OrderTransaction order = orderService.createOrder(buyerId, ownerId, credits);
        return ResponseEntity.ok(order);
    }

    /**
     * Buyer xác nhận thanh toán
     * POST /api/orders/{id}/confirm
     */
    @PostMapping("/{id}/confirm")
    public ResponseEntity<?> confirm(@PathVariable Long id) {
        try {
            OrderTransaction order = orderService.confirmPayment(id);
            return ResponseEntity.ok(order);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return orderService.getOrder(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id) {
        try {
            orderService.cancelOrder(id);
            return ResponseEntity.ok("Cancelled");
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
