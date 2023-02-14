package com.byow.wallet.byow.database.repositories;

import com.byow.wallet.byow.database.entities.ExtendedPubkeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExtendedPubkeyRepository extends JpaRepository<ExtendedPubkeyEntity, Long> {
}
