package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.domains.ScriptConfig;
import com.byow.wallet.byow.domains.node.ErrorMessages;
import com.byow.wallet.byow.gui.exceptions.CreateTransactionException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScriptConfigFinder {
    private final List<ScriptConfig> scriptConfigs;

    public ScriptConfigFinder(List<ScriptConfig> scriptConfigs) {
        this.scriptConfigs = scriptConfigs;
    }

    public ScriptConfig findByAddress(String address) {
        return scriptConfigs.stream()
            .filter(scriptConfig -> scriptConfig.addressMatcher().test(address))
            .findFirst()
            .orElseThrow(() -> new CreateTransactionException(ErrorMessages.INVALID_ADDRESS));
    }
}
