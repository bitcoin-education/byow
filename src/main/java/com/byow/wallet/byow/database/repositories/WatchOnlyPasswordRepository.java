package com.byow.wallet.byow.database.repositories;

import com.byow.wallet.byow.database.entities.WatchOnlyPassword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchOnlyPasswordRepository extends JpaRepository<WatchOnlyPassword, Long> {
}
