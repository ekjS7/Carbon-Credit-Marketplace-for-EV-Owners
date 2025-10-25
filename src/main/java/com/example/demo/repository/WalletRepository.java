package com.example.demo.repository;

import com.example.demo.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

<<<<<<< HEAD
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
=======
@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Wallet findByUserId(Long userId);
>>>>>>> a2a4f0ac6d5adbf499f884d29ff69d42b5ebf1d2
}
