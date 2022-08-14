package com.byow.wallet.byow.gui

import com.byow.wallet.byow.api.services.NodeEstimateFeeService
import com.byow.wallet.byow.observables.AddressRow
import com.byow.wallet.byow.observables.TransactionRow
import com.byow.wallet.byow.utils.BitcoinFormatter
import com.byow.wallet.byow.utils.Satoshi
import javafx.scene.control.Label
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import org.springframework.boot.test.mock.mockito.MockBean

import java.util.stream.IntStream

import static java.util.concurrent.TimeUnit.SECONDS
import static org.mockito.Mockito.when

class SendBitcoinAdditionalTest extends GuiTest {
    @MockBean
    NodeEstimateFeeService nodeEstimateFeeService

    def setup() {
        loadWalletAndAddBalance()
        when(nodeEstimateFeeService.estimate()).thenReturn(0.0002)
    }

    def "should send bitcoin with password"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write("My Test Wallet 15")
            clickOn("#password")
            String password = "mytestpassword"
            write(password)
            clickOn("Create")
            clickOn("OK")
            clickOn("Receive")
            waitLoadWallet()
            BigDecimal funds = 0
            IntStream.range(0, previousUtxosNumber).forEach {
                String address = lookup("#receivingAddress").queryAs(TextField).text
                BigDecimal amount = 1.0
                sendBitcoinAndWait(address, 1.0, 1, "#addressesTable", amount)
                funds += amount
            }
            String nodeAddress = nodeGetNewAddressClient.getNewAddress(TESTWALLET, "bech32")
            nodeGenerateToAddressClient.generateToAddress(TESTWALLET, 1, nodeAddress)
            clickOn("#sendTab")
            clickOn("#amountToSend")
            write(amountToSend)
            clickOn("#addressToSend")
            write(nodeAddress)
            clickOn("#send")
            waitForDialog()
            String amountToSendLabel = lookup("#amountToSendDialog").queryAs(Label).text
            String totalFeeLabel = lookup("#totalFees").queryAs(Label).text
            String totalLabel = lookup("#total").queryAs(Label).text
            String feeRateLabel = lookup("#feeRate").queryAs(Label).text
            String addressToSendLabel = lookup("#addressToSendDialog").queryAs(Label).text
            clickOn("#walletPassword")
            write(password)
            clickOn("OK")
            sleep(TIMEOUT, SECONDS)
            TableView<AddressRow> addressesTable = lookup("#addressesTable").queryAs(TableView)
            clickOn("#transactionsTab")
            TableView<TransactionRow> transactionsTable = lookup("#transactionsTable").queryAs(TableView)
            String labelText = lookup("#totalBalance").queryAs(Label).getText()
        then:
            addressesTable.items.size() == 1
            addressesTable.items[0].balance == changeAmount
            transactionsTable.items.size() == previousUtxosNumber + 1
            transactionsTable.items[0].balance == "-".concat(totalSpent)
            amountToSendLabel == BitcoinFormatter.format(new BigDecimal(amountToSend))
            totalFeeLabel == totalFee
            totalLabel == totalSpent
            feeRateLabel == feeRate
            addressToSendLabel == nodeAddress
            labelText == "Total Balance: $changeAmount BTC (confirmed: ${BitcoinFormatter.format(funds)}, unconfirmed: ${"-".concat(totalSpent)})"
        where:
            previousUtxosNumber | amountToSend | totalFee         | totalSpent    | changeAmount | feeRate
            1                   | "0.5"        | "0.00002679"     | "0.50002679"  | "0.49997321" | "0.0002 BTC/kvByte"
    }

    def "should send bitcoin without change and confirm"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write("My Test Wallet 16")
            clickOn("Create")
            clickOn("OK")
            clickOn("Receive")
            waitLoadWallet()
            BigDecimal funds = 0
            IntStream.range(0, previousUtxosNumber).forEach {
                String address = lookup("#receivingAddress").queryAs(TextField).text
                sendBitcoinAndWait(address, fundAmount, 1, "#addressesTable", fundAmount)
                funds += fundAmount
            }
            String nodeAddress = nodeGetNewAddressClient.getNewAddress(TESTWALLET, "bech32")
            nodeGenerateToAddressClient.generateToAddress(TESTWALLET, 1, nodeAddress)
            clickOn("#sendTab")
            clickOn("#amountToSend")
            write(amountToSend)
            clickOn("#addressToSend")
            write(nodeAddress)
            clickOn("#send")
            waitForDialog()
            String amountToSendLabel = lookup("#amountToSendDialog").queryAs(Label).text
            String totalFeeLabel = lookup("#totalFees").queryAs(Label).text
            String totalLabel = lookup("#total").queryAs(Label).text
            String feeRateLabel = lookup("#feeRate").queryAs(Label).text
            String addressToSendLabel = lookup("#addressToSendDialog").queryAs(Label).text
            clickOn("OK")
            sleep(TIMEOUT, SECONDS)
            nodeGenerateToAddressClient.generateToAddress(TESTWALLET, 1, nodeAddress)
            TableView<AddressRow> addressesTable = lookup("#addressesTable").queryAs(TableView)
            clickOn("#transactionsTab")
            TableView<TransactionRow> transactionsTable = lookup("#transactionsTable").queryAs(TableView)
            String labelText = lookup("#totalBalance").queryAs(Label).getText()
        then:
            addressesTable.items.size() == 0
            transactionsTable.items.size() == previousUtxosNumber + 1
            transactionsTable.items[0].balance == "-".concat(totalSpent)
            transactionsTable.items[0].confirmations == 1
            amountToSendLabel == BitcoinFormatter.format(new BigDecimal(amountToSend))
            totalFeeLabel == totalFee
            totalLabel == totalSpent
            feeRateLabel == feeRate
            addressToSendLabel == nodeAddress
            labelText == "Total Balance: $changeAmount BTC (confirmed: $changeAmount, unconfirmed: 0.00000000)"
        where:
            previousUtxosNumber | amountToSend | totalFee         | totalSpent    | changeAmount | fundAmount | feeRate
            1                   | "1.00000000" | "0.00002090"     | "1.00002090"  | "0.00000000" | 1.00002090 | "0.0002 BTC/kvByte"
            1                   | "1.00000000" | "0.00002383"     | "1.00002383"  | "0.00000000" | 1.00002383 | "0.0002 BTC/kvByte"
            1                   | "1.00000000" | "0.00002384"     | "1.00002384"  | "0.00000000" | 1.00002384 | "0.0002 BTC/kvByte"
    }

    def "should send bitcoin to our own address"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write("My Test Wallet 17")
            clickOn("Create")
            clickOn("OK")
            clickOn("Receive")
            waitLoadWallet()
            BigDecimal funds = 0
            String lastUsedAddress = ""
            IntStream.range(0, previousUtxosNumber).forEach {
                String address = lookup("#receivingAddress").queryAs(TextField).text
                BigDecimal amount = 1.0
                sendBitcoinAndWait(address, 1.0, it+1, "#addressesTable", amount)
                funds += amount
                lastUsedAddress = address
            }
            String nodeAddress = nodeGetNewAddressClient.getNewAddress(TESTWALLET, "bech32")
            nodeGenerateToAddressClient.generateToAddress(TESTWALLET, 1, nodeAddress)
            clickOn("#sendTab")
            clickOn("#amountToSend")
            write(amountToSend)
            clickOn("#addressToSend")
            write(lastUsedAddress)
            clickOn("#send")
            waitForDialog()
            String amountToSendLabel = lookup("#amountToSendDialog").queryAs(Label).text
            String totalFeeLabel = lookup("#totalFees").queryAs(Label).text
            String totalLabel = lookup("#total").queryAs(Label).text
            String feeRateLabel = lookup("#feeRate").queryAs(Label).text
            String addressToSendLabel = lookup("#addressToSendDialog").queryAs(Label).text
            clickOn("OK")
            sleep(TIMEOUT, SECONDS)
            TableView<AddressRow> addressesTable = lookup("#addressesTable").queryAs(TableView)
            clickOn("#transactionsTab")
            TableView<TransactionRow> transactionsTable = lookup("#transactionsTable").queryAs(TableView)
            String labelText = lookup("#totalBalance").queryAs(Label).getText()
        then:
            addressesTable.items.size() == 2
            addressesTable.items.any {
                it.balance == changeAmount
            }
            addressesTable.items.any {
                it.balance == BitcoinFormatter.format(new BigDecimal(amountToSend))
            }
            transactionsTable.items.size() == previousUtxosNumber + 1
            transactionsTable.items[0].balance == "-".concat(totalSpent)
            amountToSendLabel == BitcoinFormatter.format(new BigDecimal(amountToSend))
            totalFeeLabel == totalFee
            totalLabel == totalSpent
            feeRateLabel == feeRate
            addressToSendLabel == lastUsedAddress
            labelText == "Total Balance: $totalBalance BTC (confirmed: ${BitcoinFormatter.format(funds)}, unconfirmed: ${"-".concat(totalSpent)})"
        where:
            previousUtxosNumber | amountToSend | totalFee         | totalSpent    | changeAmount | totalBalance | feeRate
            1                   | "0.5"        | "0.00002679"     | "0.00002679"  | "0.49997321" | "0.99997321" | "0.0002 BTC/kvByte"
            2                   | "1.5"        | "0.00003971"     | "0.00003971"  | "0.49996029" | "1.99996029" | "0.0002 BTC/kvByte"
    }

    def "should send bitcoin additional test"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write("My Test Wallet 17")
            clickOn("Create")
            clickOn("OK")
            clickOn("Receive")
            waitLoadWallet()
            BigDecimal funds = 0
            IntStream.range(0, previousUtxosNumber).forEach {
                String address = lookup("#receivingAddress").queryAs(TextField).text
                BigDecimal amount = Satoshi.toBtc(utxoAmounts[it])
                sendBitcoinAndWait(address, amount, it+1, "#addressesTable", amount)
                funds += amount
            }
            String nodeAddress = nodeGetNewAddressClient.getNewAddress(TESTWALLET, "bech32")
            nodeGenerateToAddressClient.generateToAddress(TESTWALLET, 1, nodeAddress)
            clickOn("#sendTab")
            clickOn("#amountToSend")
            String amountToSend = "1"
            write(amountToSend)
            clickOn("#addressToSend")
            write(nodeAddress)
            clickOn("#send")
            waitForDialog()
            String amountToSendLabel = lookup("#amountToSendDialog").queryAs(Label).text
            String totalFeeLabel = lookup("#totalFees").queryAs(Label).text
            String totalLabel = lookup("#total").queryAs(Label).text
            String feeRateLabel = lookup("#feeRate").queryAs(Label).text
            String addressToSendLabel = lookup("#addressToSendDialog").queryAs(Label).text
            clickOn("OK")
            sleep(TIMEOUT, SECONDS)
            TableView<AddressRow> addressesTable = lookup("#addressesTable").queryAs(TableView)
            clickOn("#transactionsTab")
            TableView<TransactionRow> transactionsTable = lookup("#transactionsTable").queryAs(TableView)
            String labelText = lookup("#totalBalance").queryAs(Label).getText()
        then:
            if (changeIsDust) {
                addressesTable.items.size() == 0
            } else {
                addressesTable.items.size() == 1
                addressesTable.items[0].balance == changeAmount
            }
            transactionsTable.items.size() == previousUtxosNumber + 1
            transactionsTable.items[0].balance == "-".concat(totalSpent)
            amountToSendLabel == BitcoinFormatter.format(new BigDecimal(amountToSend))
            totalFeeLabel == totalFee
            totalLabel == totalSpent
            feeRateLabel == "0.0002 BTC/kvByte"
            addressToSendLabel == nodeAddress
            labelText == "Total Balance: $changeAmount BTC (confirmed: ${BitcoinFormatter.format(funds)}, unconfirmed: ${"-".concat(totalSpent)})"
        where:
            previousUtxosNumber | totalFee                 | utxoAmounts                          | totalSpent    | changeAmount | changeIsDust
            1                   | "0.00002090"             | [100_002_090]                        | "1.00002090"  | "0.00000000" | true
            1                   | "0.00002091"             | [100_002_091]                        | "1.00002091"  | "0.00000000" | true
            1                   | "0.00002383"             | [100_002_383]                        | "1.00002383"  | "0.00000000" | true
            1                   | "0.00002384"             | [100_002_384]                        | "1.00002384"  | "0.00000000" | true
            1                   | "0.00002678"             | [100_002_678]                        | "1.00002678"  | "0.00000000" | true
            1                   | "0.00002679"             | [100_002_679]                        | "1.00002679"  | "0.00000000" | true
            1                   | "0.00002972"             | [100_002_972]                        | "1.00002972"  | "0.00000000" | true
            1                   | "0.00002679"             | [100_002_973]                        | "1.00002679"  | "0.00000294" | false
            2                   | "0.00003382"             | [50_001_691, 50_001_691]             | "1.00003382"  | "0.00000000" | true
            2                   | "0.00003383"             | [50_001_691, 50_001_692]             | "1.00003383"  | "0.00000000" | true
            2                   | "0.00003675"             | [50_001_691, 50_001_984]             | "1.00003675"  | "0.00000000" | true
            2                   | "0.00003676"             | [50_001_691, 50_001_985]             | "1.00003676"  | "0.00000000" | true
            2                   | "0.00003970"             | [50_001_691, 50_002_279]             | "1.00003970"  | "0.00000000" | true
            2                   | "0.00003971"             | [50_001_691, 50_002_280]             | "1.00003971"  | "0.00000000" | true
            2                   | "0.00004264"             | [50_001_691, 50_002_573]             | "1.00004264"  | "0.00000000" | true
            2                   | "0.00003971"             | [50_001_691, 50_002_574]             | "1.00003971"  | "0.00000294" | false
            3                   | "0.00004674"             | [33_334_891, 33_334_891, 33_334_892] | "1.00004674"  | "0.00000000" | true
            3                   | "0.00004675"             | [33_334_891, 33_334_891, 33_334_893] | "1.00004675"  | "0.00000000" | true
            3                   | "0.00004967"             | [33_334_891, 33_334_891, 33_335_185] | "1.00004967"  | "0.00000000" | true
            3                   | "0.00004968"             | [33_334_891, 33_334_891, 33_335_186] | "1.00004968"  | "0.00000000" | true
            3                   | "0.00005262"             | [33_334_891, 33_334_891, 33_335_480] | "1.00005262"  | "0.00000000" | true
            3                   | "0.00005263"             | [33_334_891, 33_334_891, 33_335_481] | "1.00005263"  | "0.00000000" | true
            3                   | "0.00005556"             | [33_334_891, 33_334_891, 33_335_774] | "1.00005556"  | "0.00000000" | true
            3                   | "0.00005263"             | [33_334_891, 33_334_891, 33_335_775] | "1.00005263"  | "0.00000294" | false
    }
}