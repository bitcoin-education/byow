package com.byow.wallet.byow.database.services;

import com.byow.wallet.byow.database.entities.ExtendedPubkeyEntity;
import com.byow.wallet.byow.database.entities.WalletEntity;
import com.byow.wallet.byow.database.entities.WatchOnlyPassword;
import com.byow.wallet.byow.database.repositories.ExtendedPubkeyRepository;
import com.byow.wallet.byow.database.repositories.WatchOnlyPasswordRepository;
import com.byow.wallet.byow.domains.Wallet;
import org.springframework.stereotype.Service;

@Service
public class SaveWatchOnlyWalletService {
    public final SaveWalletService saveWalletService;

    private final ExtendedPubkeyRepository extendedPubkeyRepository;

    private final WatchOnlyPasswordRepository watchOnlyPasswordRepository;

    public SaveWatchOnlyWalletService(
        SaveWalletService saveWalletService,
        ExtendedPubkeyRepository extendedPubkeyRepository,
        WatchOnlyPasswordRepository watchOnlyPasswordRepository
    ) {
        this.saveWalletService = saveWalletService;
        this.extendedPubkeyRepository = extendedPubkeyRepository;
        this.watchOnlyPasswordRepository = watchOnlyPasswordRepository;
    }

    public void saveWallet(Wallet wallet, String password) {
        WalletEntity walletEntity = saveWalletService.saveWallet(wallet);
        saveExtendedPubkeys(wallet, walletEntity);
        saveWatchOnlyPassword(walletEntity, password);
    }

    private void saveWatchOnlyPassword(WalletEntity walletEntity, String password) {
        watchOnlyPasswordRepository.save(new WatchOnlyPassword(password, walletEntity));
    }

    private void saveExtendedPubkeys(Wallet wallet, WalletEntity walletEntity) {
        wallet.extendedPubkeys()
            .stream()
            .map(extendedPubkey -> new ExtendedPubkeyEntity(extendedPubkey.getKey(), extendedPubkey.getType(), walletEntity))
            .forEach(extendedPubkeyRepository::save);
    }
}
