package com.example.demo.repository;

import com.example.demo.entity.CarbonWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CarbonWalletRepository extends JpaRepository<CarbonWallet, Long> {

    // vì trong entity `CarbonWallet` field là `owner` (User),
    // nên phương thức truy vấn theo owner.id sẽ viết như sau:
    Optional<CarbonWallet> findByOwner_Id(Long ownerId);
}
