package com.byow.wallet.byow.database.repositories;

import com.byow.wallet.byow.database.entities.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface WalletRepository extends JpaRepository<WalletEntity, Long> {
    WalletEntity findByName(String name);

    @Transactional
    @Modifying
    @Query("update WalletEntity w set w.numberOfGeneratedAddresses = w.numberOfGeneratedAddresses + :increment where w.name = :name")
    void incrementNumberOfGeneratedAddresses(@Param("increment") Integer increment, @Param("name") String name);

    boolean existsByName(String walletName);
}
