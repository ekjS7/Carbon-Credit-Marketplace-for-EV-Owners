package com.example.demo.service;

import com.example.demo.entity.CarbonCredit;
import com.example.demo.entity.CarbonHolding;
import com.example.demo.entity.CarbonWallet;
import com.example.demo.entity.User;
import com.example.demo.repository.CarbonHoldingRepository;
import com.example.demo.repository.CarbonWalletRepository;
import com.example.demo.repository.CarbonCreditRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

/**
 * Service chịu trách nhiệm quản lý tín chỉ carbon (không phải tiền).
 * - Mỗi user có 1 CarbonWallet
 * - CarbonHolding = từng loại tín chỉ + số lượng
 */
@Service
@RequiredArgsConstructor
public class CarbonWalletService {

    private final CarbonWalletRepository carbonWalletRepository;
    private final CarbonHoldingRepository carbonHoldingRepository;
    private final CarbonCreditRepository carbonCreditRepository;
    private final UserRepository userRepository;

    /**
     * Lấy (hoặc tạo mới) ví carbon cho user.
     */
    @Transactional
    public CarbonWallet getOrCreateCarbonWallet(Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

        return carbonWalletRepository.findByOwner(owner)
                .orElseGet(() -> {
                    CarbonWallet w = new CarbonWallet();
                    w.setOwner(owner);
                    return carbonWalletRepository.save(w);
                });
    }

    /**
     * +qty tín chỉ creditId vào ví userId (buyer mua)
     */
    @Transactional
    public void addHolding(Long userId, Long creditId, int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be > 0");
        }

        CarbonWallet wallet = getOrCreateCarbonWallet(userId);

        CarbonCredit credit = carbonCreditRepository.findById(creditId)
                .orElseThrow(() -> new NoSuchElementException("Carbon credit not found: " + creditId));

        CarbonHolding holding = carbonHoldingRepository
                .findByCarbonWalletAndCredit(wallet, credit)
                .orElseGet(() -> {
                    CarbonHolding h = new CarbonHolding();
                    h.setCarbonWallet(wallet);
                    h.setCredit(credit);
                    h.setQuantity(0);
                    return h;
                });

        holding.setQuantity(holding.getQuantity() + qty);
        carbonHoldingRepository.save(holding);
    }

    /**
     * -qty tín chỉ creditId từ ví userId (seller bán)
     */
    @Transactional
    public void removeHolding(Long userId, Long creditId, int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be > 0");
        }

        CarbonWallet wallet = getOrCreateCarbonWallet(userId);

        CarbonCredit credit = carbonCreditRepository.findById(creditId)
                .orElseThrow(() -> new NoSuchElementException("Carbon credit not found: " + creditId));

        CarbonHolding holding = carbonHoldingRepository
                .findByCarbonWalletAndCredit(wallet, credit)
                .orElseThrow(() -> new IllegalStateException("User does not own this credit"));

        if (holding.getQuantity() < qty) {
            throw new IllegalStateException("Not enough credits to transfer");
        }

        holding.setQuantity(holding.getQuantity() - qty);

        if (holding.getQuantity() == 0) {
            carbonHoldingRepository.delete(holding);
        } else {
            carbonHoldingRepository.save(holding);
        }
    }

    /**
     * Chuyển quyền sở hữu tín chỉ (seller -> buyer).
     * Đây là bước "giao hàng" sau khi tiền đã được xử lý bởi WalletService.
     */
    @Transactional
    public void transferHolding(Long sellerId, Long buyerId, Long creditId, int qty) {
        // 1. trừ tín chỉ của seller
        removeHolding(sellerId, creditId, qty);

        // 2. cộng tín chỉ cho buyer
        addHolding(buyerId, creditId, qty);
    }
}
