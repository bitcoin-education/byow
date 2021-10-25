package com.byow.wallet.byow.domains;

import com.byow.wallet.byow.api.services.AddressGenerator;

public record AddressConfig(AddressType addressType, String derivationPath, AddressGenerator addressGenerator) {
}
