package com.example.demo.controller;

import com.example.demo.dto.CreateTopupRequest;
import com.example.demo.service.VnPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment/vnpay")
@RequiredArgsConstructor
@Slf4j
public class VnPayController {

    private final VnPayService vnPayService;

    /**
     * Tạo URL thanh toán VNPay
     */
    @PostMapping("/create")
    public ResponseEntity<?> createPayment(
            @RequestBody CreateTopupRequest request,
            HttpServletRequest httpServletRequest
    ) {
        try {
            log.info("=== VNPay Create Payment Request ===");
            log.info("UserId: {}, Amount: {}", request.getUserId(), request.getAmount());
            
            String paymentUrl = vnPayService.createPaymentUrl(request, httpServletRequest);
            
            log.info("Payment URL created successfully");
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "paymentUrl", paymentUrl,
                    "message", "Payment URL created successfully"
            ));
            
        } catch (IllegalArgumentException e) {
            // Validation errors (bad request from user)
            log.warn("Invalid VNPay topup request: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "message", "Invalid topup request",
                            "error", e.getMessage()
                    ));
            
        } catch (IllegalStateException e) {
            // Configuration errors
            log.error("VNPay configuration error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "VNPAY not configured properly",
                            "error", e.getMessage(),
                            "hint", "Please check application-dev.yml and configure tmn-code and hash-secret"
                    ));
            
        } catch (Exception e) {
            // Unexpected errors
            log.error("Unexpected error creating VNPay payment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Failed to create VNPAY payment",
                            "error", e.getMessage(),
                            "type", e.getClass().getSimpleName()
                    ));
        }
    }

    /**
     * IPN (Instant Payment Notification) callback từ VNPay
     * Endpoint này được VNPay gọi để thông báo kết quả thanh toán
     */
    @GetMapping("/ipn")
    public ResponseEntity<Map<String, String>> handleIpn(HttpServletRequest request) {
        try {
            log.info("Received VNPay IPN callback");
            
            // Extract all parameters
            Map<String, String> params = extractParams(request);
            
            // Log parameters (không log sensitive data trong production)
            log.debug("IPN params: {}", params);
            
            // Verify signature
            if (!vnPayService.verifyIpn(params)) {
                log.error("Invalid VNPay IPN signature");
                return ResponseEntity.ok(Map.of(
                        "RspCode", "97",
                        "Message", "Invalid signature"
                ));
            }
            
            // Process payment
            vnPayService.processIpn(params);
            
            // Return success response to VNPay
            return ResponseEntity.ok(Map.of(
                    "RspCode", "00",
                    "Message", "Confirm Success"
            ));
            
        } catch (Exception e) {
            log.error("Error processing VNPay IPN: {}", e.getMessage(), e);
            return ResponseEntity.ok(Map.of(
                    "RspCode", "99",
                    "Message", "Unknown error: " + e.getMessage()
            ));
        }
    }

    /**
     * Return URL - Frontend redirect về đây sau khi user hoàn thành thanh toán
     * Endpoint này không bắt buộc vì frontend sẽ tự xử lý query params,
     * nhưng có thể dùng để validate lại trạng thái
     */
    @GetMapping("/return")
    public ResponseEntity<?> handleReturn(HttpServletRequest request) {
        try {
            Map<String, String> params = extractParams(request);
            
            // Verify signature
            if (!vnPayService.verifyIpn(params)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid signature"));
            }
            
            String vnpTxnRef = params.get("vnp_TxnRef");
            String vnpTransactionStatus = params.get("vnp_TransactionStatus");
            String vnpAmount = params.get("vnp_Amount");
            
            return ResponseEntity.ok(Map.of(
                    "success", "00".equals(vnpTransactionStatus),
                    "txnRef", vnpTxnRef,
                    "amount", vnpAmount,
                    "status", vnpTransactionStatus,
                    "message", "00".equals(vnpTransactionStatus) 
                            ? "Payment successful" 
                            : "Payment failed or cancelled"
            ));
            
        } catch (Exception e) {
            log.error("Error handling VNPay return: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Extract all query parameters from request
     */
    private Map<String, String> extractParams(HttpServletRequest request) {
        Map<String, String> result = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = request.getParameter(paramName);
            result.put(paramName, paramValue);
        }
        
        return result;
    }
}

