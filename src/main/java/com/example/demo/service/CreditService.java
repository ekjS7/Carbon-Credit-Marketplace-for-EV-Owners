package com.example.demo.service;

import com.example.demo.entity.CreditRequest;
import com.example.demo.repository.CreditRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreditService {

    private final CreditRequestRepository creditRequestRepository;
    private final CarbonCreditService carbonCreditService;
    private final WalletService walletService; // connect sang ví

    // EV Owner gửi yêu cầu
    public CreditRequest submitRequest(CreditRequest request) {
        request.setStatus("PENDING");
        return creditRequestRepository.save(request);
    }

    // CVA duyệt yêu cầu
    @Transactional
    public CreditRequest approveRequest(Long requestId) {
        CreditRequest request = creditRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!"PENDING".equals(request.getStatus())) {
            throw new IllegalStateException("Request already processed");
        }

        request.setStatus("APPROVED");
        creditRequestRepository.save(request);

        // Tạo bản ghi CarbonCredit
        var credit = carbonCreditService.issueCredit(request);

        // Cộng tín chỉ vào Wallet
        walletService.credit(request.getOwnerId(), credit.getAmount(),
                "Issued from CreditRequest#" + request.getId());

        return request;
    }

    // CVA từ chối
    public CreditRequest rejectRequest(Long requestId, String reason) {
        CreditRequest request = creditRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus("REJECTED: " + reason);
        return creditRequestRepository.save(request);
    }
}
