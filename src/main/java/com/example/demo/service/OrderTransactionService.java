package com.example.demo.service;

import com.example.demo.entity.OrderTransaction;
import com.example.demo.repository.OrderTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderTransactionService {

    private final OrderTransactionRepository orderRepo;
    private final WalletService walletService; // sử dụng service hiện có để chuyển tín chỉ

    /**
     * Buyer ấn mua -> tạo order, set status = IN_TRANSACTION
     */
    public OrderTransaction createOrder(Long buyerId, Long ownerId, BigDecimal creditsAmount) {
        OrderTransaction order = OrderTransaction.builder()
                .buyerId(buyerId)
                .ownerId(ownerId)
                .creditsAmount(creditsAmount)
                .status("IN_TRANSACTION")
                .build();
        return orderRepo.save(order);
    }

    /**
     * Buyer confirm thanh toán -> finalize transaction:
     * - gọi walletService.transferCredits(ownerId, buyerId, creditsAmount)
     * - cập nhật order.status = SUCCESS hoặc FAILED nếu có exception
     *
     * Important: toàn bộ thao tác thực hiện trong DB transaction
     */
    @Transactional
    public OrderTransaction confirmPayment(Long orderId) {
        OrderTransaction order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (!"IN_TRANSACTION".equals(order.getStatus())) {
            // idempotency / trạng thái: nếu đã SUCCESS trả về luôn
            if ("SUCCESS".equals(order.getStatus())) return order;
            throw new IllegalStateException("Order is not in IN_TRANSACTION state: " + order.getStatus());
        }

        try {
            // gọi WalletService thực hiện trừ Owner và cộng Buyer (gồm ghi WalletTransaction và update Wallet)
            walletService.transferCredits(order.getOwnerId(), order.getBuyerId(), order.getCreditsAmount());

            order.setStatus("SUCCESS");
            order.setUpdatedAt(LocalDateTime.now());
            return orderRepo.save(order);
        } catch (Exception ex) {
            // mark failed và propagate hoặc log
            order.setStatus("FAILED");
            order.setUpdatedAt(LocalDateTime.now());
            orderRepo.save(order);
            throw ex;
        }
    }

    public Optional<OrderTransaction> getOrder(Long id) {
        return orderRepo.findById(id);
    }

    public void cancelOrder(Long id) {
        OrderTransaction order = orderRepo.findById(id).orElseThrow();
        if ("SUCCESS".equals(order.getStatus())) {
            throw new IllegalStateException("Cannot cancel completed order");
        }
        order.setStatus("CANCELLED");
        order.setUpdatedAt(LocalDateTime.now());
        orderRepo.save(order);
    }
}
