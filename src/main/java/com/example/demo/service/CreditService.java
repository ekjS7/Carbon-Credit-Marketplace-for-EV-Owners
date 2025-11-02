package com.example.demo.service;

import com.example.demo.entity.CreditRequest;
import com.example.demo.repository.CreditRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditService {

    private final CreditRequestRepository creditRequestRepository;
    private final WalletService walletService;

    @Transactional
    public CreditRequest submitRequest(CreditRequest req) {
        req.setStatus("PENDING");
        req.setCreatedAt(LocalDateTime.now());
        req.setUpdatedAt(LocalDateTime.now());
        CreditRequest saved = creditRequestRepository.save(req);

        log.info("[CreditService] submitRequest userId={}, amount={}, id={}",
                saved.getUserId(), saved.getAmount(), saved.getId());
        return saved;
    }

    @Transactional
    public CreditRequest approveRequest(Long requestId) {
        CreditRequest req = creditRequestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("CreditRequest not found: " + requestId));

        if (!"PENDING".equals(req.getStatus())) {
            if ("APPROVED".equals(req.getStatus())) return req;
            throw new IllegalStateException("Request is not PENDING: " + req.getStatus());
        }

        Long userId = req.getUserId();
        BigDecimal amount = req.getAmount();

        // trừ tiền từ ví user và tạo bản ghi withdraw pending
        walletService.requestWithdraw(
                userId,
                amount,
                "Withdraw approved for request " + requestId
        );

        req.setStatus("APPROVED");
        req.setUpdatedAt(LocalDateTime.now());
        creditRequestRepository.save(req);

        log.info("[CreditService] approveRequest id={}, userId={}, amount={}",
                requestId, userId, amount);

        return req;
    }

    @Transactional
    public CreditRequest rejectRequest(Long requestId, String reason) {
        CreditRequest req = creditRequestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("CreditRequest not found: " + requestId));

        if (!"PENDING".equals(req.getStatus())) {
            if ("REJECTED".equals(req.getStatus())) return req;
            throw new IllegalStateException("Request is not PENDING: " + req.getStatus());
        }

        req.setStatus("REJECTED");
        req.setRejectReason(reason);
        req.setUpdatedAt(LocalDateTime.now());
        creditRequestRepository.save(req);

        log.warn("[CreditService] rejectRequest id={}, reason={}", requestId, reason);
        return req;
    }

    // optional: nạp tiền ví bằng VNPAY flow
    @Transactional
    public String initTopUp(Long userId, BigDecimal amount) {
        var tx = walletService.initTopUp(userId, amount, "VNPAY");
        return tx.getOrderRef();
    }

    @Transactional
    public void confirmTopUp(String orderRef) {
        walletService.confirmTopUpSuccess(orderRef);
    }

    @Transactional
    public void failTopUp(String orderRef, String reason) {
        walletService.markTopUpFailed(orderRef, reason);
    }
}
