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
    private final CarbonWalletService carbonWalletService;

    /**
     * Buyer bấm "mua" -> tạo order ở trạng thái IN_TRANSACTION
     *
     * @param buyerId     người mua
     * @param ownerId     người bán (chủ ví tín chỉ)
     * @param creditId    loại tín chỉ carbon muốn mua (CarbonCredit.id)
     * @param quantity    bao nhiêu tín chỉ
     * @param totalAmount số tiền tương ứng (có thể null nếu bạn chưa xử lý thanh toán tiền ở bước này)
     */
    public OrderTransaction createOrder(Long buyerId,
                                        Long ownerId,
                                        Long creditId,
                                        Integer quantity,
                                        BigDecimal totalAmount) {

        OrderTransaction order = OrderTransaction.builder()
                .buyerId(buyerId)
                .ownerId(ownerId)
                .creditId(creditId)
                .quantity(quantity)
                .creditsAmount(totalAmount)
                .status("IN_TRANSACTION")
                .build();

        return orderRepo.save(order);
    }

    /**
     * Buyer confirm thanh toán -> chuyển quyền sở hữu tín chỉ
     * - Trừ tín chỉ khỏi ví carbon của ownerId
     * - Cộng tín chỉ cho buyerId
     * - Cập nhật trạng thái SUCCESS
     *
     * Nếu fail (ví dụ seller không đủ tín chỉ), mark FAILED.
     */
    @Transactional
    public OrderTransaction confirmPayment(Long orderId) {
        OrderTransaction order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (!"IN_TRANSACTION".equals(order.getStatus())) {
            // idempotency: nếu đã SUCCESS thì trả ra luôn
            if ("SUCCESS".equals(order.getStatus())) {
                return order;
            }
            throw new IllegalStateException("Order is not in IN_TRANSACTION state: " + order.getStatus());
        }

        try {
            // Chuyển tín chỉ carbon
            carbonWalletService.transferHolding(
                    order.getOwnerId(),   // seller
                    order.getBuyerId(),   // buyer
                    order.getCreditId(),  // loại tín chỉ
                    order.getQuantity()   // số lượng tín chỉ
            );

            order.setStatus("SUCCESS");
            order.setUpdatedAt(LocalDateTime.now());
            return orderRepo.save(order);

        } catch (Exception e) {
            order.setStatus("FAILED");
            order.setUpdatedAt(LocalDateTime.now());
            orderRepo.save(order);
            throw e;
        }
    }

    /**
     * Xem chi tiết order theo ID
     */
    public Optional<OrderTransaction> getOrder(Long id) {
        return orderRepo.findById(id);
    }

    /**
     * Buyer hoặc seller huỷ order nếu chưa xong
     */
    @Transactional
    public void cancelOrder(Long id) {
        OrderTransaction order = orderRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));

        if ("SUCCESS".equals(order.getStatus())) {
            throw new IllegalStateException("Cannot cancel completed order");
        }

        order.setStatus("CANCELLED");
        order.setUpdatedAt(LocalDateTime.now());
        orderRepo.save(order);
    }
}
