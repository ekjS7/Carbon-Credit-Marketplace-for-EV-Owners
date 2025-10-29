package com.example.demo.service;

import com.example.demo.entity.CreditRequest;
import java.util.List;

public interface CvaService {
    List<CreditRequest> getPendingRequests();
    CreditRequest getRequestById(Long id);
    CreditRequest approveRequest(Long id, String notes);
    CreditRequest rejectRequest(Long id, String notes);
}
