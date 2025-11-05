package com.example.demo.controller;

import com.example.demo.entity.CarbonCredit;
import com.example.demo.service.CarbonCreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carbon-credits")
@RequiredArgsConstructor
public class CarbonCreditController {

    private final CarbonCreditService carbonCreditService;

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<CarbonCredit>> getByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(carbonCreditService.getCreditsByOwner(ownerId));
    }

    @GetMapping("/listed")
    public ResponseEntity<List<CarbonCredit>> getListed() {
        return ResponseEntity.ok(carbonCreditService.getListedCredits());
    }

    @PostMapping("/{id}/list")
    public ResponseEntity<CarbonCredit> listCredit(@PathVariable Long id) {
        return ResponseEntity.ok(carbonCreditService.listCredit(id));
    }

    @PostMapping("/{id}/unlist")
    public ResponseEntity<CarbonCredit> unlistCredit(@PathVariable Long id) {
        return ResponseEntity.ok(carbonCreditService.unlistCredit(id));
    }
}
