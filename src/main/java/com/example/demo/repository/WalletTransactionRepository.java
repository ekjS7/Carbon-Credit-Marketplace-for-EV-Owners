package com.example.demo.repository;

import com.example.demo.entity.WalletTransaction;
import com.example.demo.entity.WalletTransaction.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    /**
     * Lấy lịch sử giao dịch của 1 user (ví tiền), sắp xếp mới nhất trước.
     * Dùng cho trang "Lịch sử ví" của user.
     */
    List<WalletTransaction> findByWallet_User_IdOrderByCreatedAtDesc(Long userId);

    /**
     * Tìm transaction theo mã orderRef (ví dụ mã thanh toán VNPAY / mã rút tiền),
     * dùng cho:
     *  - xác nhận top-up thành công/ thất bại
     *  - tra cứu nhanh 1 giao dịch cụ thể.
     */
    Optional<WalletTransaction> findByOrderRef(String orderRef);

    /**
     * (Tuỳ chọn, hữu ích cho admin)
     * Lấy tất cả transaction theo trạng thái.
     * Ví dụ: tất cả WITHDRAW đang PENDING.
     */
    List<WalletTransaction> findAllByStatus(TransactionStatus status);
}
