package com.byow.wallet.byow.gui.listeners;

import com.byow.wallet.byow.domains.TransactionDto;
import com.byow.wallet.byow.domains.Utxo;
import com.byow.wallet.byow.gui.events.TransactionSentEvent;
import com.byow.wallet.byow.gui.services.UpdateCurrentWalletAddressesService;
import com.byow.wallet.byow.gui.services.UpdateCurrentWalletBalanceService;
import com.byow.wallet.byow.gui.services.UpdateCurrentWalletTransactionsService;
import com.byow.wallet.byow.observables.TransactionRow;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class TransactionSentListener implements ApplicationListener<TransactionSentEvent> {
    private final UpdateCurrentWalletAddressesService updateCurrentWalletAddressesService;

    private final UpdateCurrentWalletTransactionsService updateCurrentWalletTransactionsService;

    private final UpdateCurrentWalletBalanceService updateCurrentWalletBalanceService;

    public TransactionSentListener(
        UpdateCurrentWalletAddressesService updateCurrentWalletAddressesService,
        UpdateCurrentWalletTransactionsService updateCurrentWalletTransactionsService,
        UpdateCurrentWalletBalanceService updateCurrentWalletBalanceService
    ) {
        this.updateCurrentWalletAddressesService = updateCurrentWalletAddressesService;
        this.updateCurrentWalletTransactionsService = updateCurrentWalletTransactionsService;
        this.updateCurrentWalletBalanceService = updateCurrentWalletBalanceService;
    }

    @Override
    public void onApplicationEvent(TransactionSentEvent event) {
        TransactionDto transactionDto = event.getTransactionDto();
        List<Utxo> utxos = transactionDto.selectedUtxos().stream()
            .map(utxo -> new Utxo(
                utxo.txid(),
                utxo.vout(),
                utxo.address(),
                utxo.label(),
                utxo.scriptPubKey(),
                BigDecimal.ZERO,
                utxo.confirmations(),
                utxo.redeemScript(),
                utxo.witnessScript()
            )).toList();
        updateCurrentWalletAddressesService.update(utxos);
        updateCurrentWalletTransactionsService.update(TransactionRow.from(transactionDto));
        updateCurrentWalletBalanceService.update();
    }
}
