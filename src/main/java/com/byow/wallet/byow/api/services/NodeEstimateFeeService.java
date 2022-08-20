package com.byow.wallet.byow.api.services;

import com.byow.wallet.byow.api.services.node.client.NodeEstimateSmartFeeClient;
import com.byow.wallet.byow.domains.node.NodeFee;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class NodeEstimateFeeService implements EstimateFeeService {
    private final NodeEstimateSmartFeeClient nodeEstimateSmartFeeClient;
    private final BigDecimal fallbackFeeRate;

    public NodeEstimateFeeService(
        NodeEstimateSmartFeeClient nodeEstimateSmartFeeClient,
        @Value("${bitcoin.fallbackFeeRate}") BigDecimal fallbackFeeRate
    ) {
        this.nodeEstimateSmartFeeClient = nodeEstimateSmartFeeClient;
        this.fallbackFeeRate = fallbackFeeRate;
    }

    @Override
    public BigDecimal estimate() {
        NodeFee nodeFee = nodeEstimateSmartFeeClient.estimateSmartFee(1);
        if (nodeFee.feerate() == null) {
            return fallbackFeeRate;
        }
        return BigDecimal.valueOf(nodeFee.feerate());
    }
}
