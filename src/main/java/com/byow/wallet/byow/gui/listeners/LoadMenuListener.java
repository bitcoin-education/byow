package com.byow.wallet.byow.gui.listeners;

import com.byow.wallet.byow.database.entities.WalletEntity;
import com.byow.wallet.byow.database.repositories.WalletRepository;
import com.byow.wallet.byow.gui.events.GuiStartedEvent;
import com.byow.wallet.byow.observables.LoadMenu;
import javafx.application.Platform;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class LoadMenuListener implements ApplicationListener<GuiStartedEvent> {
    private final WalletRepository walletRepository;

    private final LoadMenu loadMenu;

    public LoadMenuListener(WalletRepository walletRepository, LoadMenu loadMenu) {
        this.walletRepository = walletRepository;
        this.loadMenu = loadMenu;
    }

    @Override
    public void onApplicationEvent(GuiStartedEvent event) {
        walletRepository.findAll().forEach(this::addWallet);
    }

    private void addWallet(WalletEntity walletEntity) {
        Platform.runLater(() -> loadMenu.add(walletEntity));
    }
}
