package com.example.demo.service;

import com.example.demo.entity.CreditRequest;
import java.util.List;

public interface CvaService {
    List<CreditRequest> getPendingRequests();
    CreditRequest getRequestById(Long id);
    void approveRequest(Long id, Long verifierId);
    void rejectRequest(Long id, Long verifierId, String reason);
}
