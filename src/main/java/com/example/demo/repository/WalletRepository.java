package com.example.demo.repository;

import com.example.demo.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    /**
     * Tìm ví (wallet) theo ID của người dùng.
     *
     * @param userId ID của người dùng
     * @return Optional chứa Wallet nếu tìm thấy
     */
    Optional<Wallet> findByUser_Id(Long userId);
}
