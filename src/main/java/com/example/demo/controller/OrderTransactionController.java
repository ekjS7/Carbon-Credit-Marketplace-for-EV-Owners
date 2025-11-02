package com.example.demo.controller;

import com.example.demo.entity.OrderTransaction;
import com.example.demo.service.OrderTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * Controller cho luồng mua tín chỉ carbon P2P giữa seller và buyer.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderTransactionController {

    private final OrderTransactionService orderService;

    /**
     * Tạo order (Buyer ấn Mua)
     *
     * Ví dụ gọi:
     * POST /api/orders/create
     *   ?buyerId=10
     *   &ownerId=2
     *   &creditId=5
     *   &quantity=3
     *   &totalAmount=150000.00
     *
     * Ý nghĩa:
     * - buyerId: ai đang mua
     * - ownerId: ai đang bán (người sở hữu tín chỉ ban đầu)
     * - creditId: user đang mua loại tín chỉ carbon nào
     * - quantity: số lượng tín chỉ muốn mua
     * - totalAmount: tổng tiền VND cho giao dịch này
     */
    @PostMapping("/create")
    public ResponseEntity<?> create(
            @RequestParam Long buyerId,
            @RequestParam Long ownerId,
            @RequestParam Long creditId,
            @RequestParam Integer quantity,
            @RequestParam BigDecimal totalAmount
    ) {
        log.info("Creating order: buyer={}, owner={}, creditId={}, quantity={}, totalAmount={}",
                buyerId, ownerId, creditId, quantity, totalAmount);

        OrderTransaction order = orderService.createOrder(
                buyerId,
                ownerId,
                creditId,
                quantity,
                totalAmount
        );

        return ResponseEntity.ok(order);
    }

    /**
     * Buyer confirm thanh toán -> chuyển tín chỉ từ owner -> buyer,
     * đánh dấu order SUCCESS hoặc FAILED.
     *
     * POST /api/orders/{orderId}/confirm
     */
    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<?> confirm(@PathVariable Long orderId) {
        log.info("Confirming order {}", orderId);
        OrderTransaction updated = orderService.confirmPayment(orderId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Hủy order (chỉ khi chưa SUCCESS)
     *
     * POST /api/orders/{orderId}/cancel
     */
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long orderId) {
        log.info("Cancelling order {}", orderId);
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok().build();
    }

    /**
     * GET /api/orders/{orderId}
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable Long orderId) {
        return orderService.getOrder(orderId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
