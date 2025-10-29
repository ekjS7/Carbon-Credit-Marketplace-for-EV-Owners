package com.example.demo.service.impl;

import com.example.demo.entity.CreditRequest;
import com.example.demo.repository.CreditRequestRepository;
import com.example.demo.service.CvaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CvaServiceImpl implements CvaService {

    private final CreditRequestRepository creditRequestRepository;

    @Autowired
    public CvaServiceImpl(CreditRequestRepository creditRequestRepository) {
        this.creditRequestRepository = creditRequestRepository;
    }

    @Override
    public List<CreditRequest> getPendingRequests() {
        return creditRequestRepository.findByStatus("PENDING");
    }

    @Override
    public Optional<CreditRequest> getRequestById(Long id) {
        return creditRequestRepository.findById(id);
    }

    @Override
    public CreditRequest approveRequest(Long id, String notes) {
        CreditRequest request = creditRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus("APPROVED");
        request.setNotes(notes);
        return creditRequestRepository.save(request);
    }

    @Override
    public CreditRequest rejectRequest(Long id, String notes) {
        CreditRequest request = creditRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus("REJECTED");
        request.setNotes(notes);
        return creditRequestRepository.save(request);
    }
}
