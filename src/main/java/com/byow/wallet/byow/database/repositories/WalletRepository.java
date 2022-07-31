package com.byow.wallet.byow.database.repositories;

import com.byow.wallet.byow.database.entities.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<WalletEntity, Long> {
    WalletEntity findByName(String name);
}
