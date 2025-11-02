package com.example.demo.repository;

import com.example.demo.entity.CarbonHolding;
import com.example.demo.entity.CarbonWallet;
import com.example.demo.entity.CarbonCredit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarbonHoldingRepository extends JpaRepository<CarbonHolding, Long> {

    // 1 loại tín chỉ cụ thể trong 1 ví cụ thể
    Optional<CarbonHolding> findByCarbonWalletAndCredit(CarbonWallet wallet, CarbonCredit credit);

    // Lấy toàn bộ các tín chỉ mà ví này đang giữ
    List<CarbonHolding> findAllByCarbonWallet(CarbonWallet wallet);
}
