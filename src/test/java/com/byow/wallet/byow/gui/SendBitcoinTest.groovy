package com.byow.wallet.byow.gui

import com.byow.wallet.byow.api.services.NodeEstimateFeeService
import com.byow.wallet.byow.observables.AddressRow
import com.byow.wallet.byow.observables.TransactionRow
import com.byow.wallet.byow.utils.BitcoinFormatter
import javafx.scene.control.Label
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import org.springframework.boot.test.mock.mockito.MockBean
import org.testfx.service.query.NodeQuery

import java.util.stream.IntStream

import static java.util.concurrent.TimeUnit.SECONDS
import static org.mockito.Mockito.when

class SendBitcoinTest extends GuiTest {
    @MockBean
    NodeEstimateFeeService nodeEstimateFeeService

    def setup() {
        loadWalletAndAddBalance()
        when(nodeEstimateFeeService.estimate()).thenReturn(0.0002)
    }

    def "should send bitcoin"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            def walletName = "My Test Wallet 9"
            write(walletName)
            clickOn("Create")
            clickOn("OK")
            clickOn("Receive")
            waitLoadWallet()

            BigDecimal funds = 0
            IntStream.range(0, previousUtxosNumber).forEach{
                String address = lookup("#receivingAddress").queryAs(TextField).text
                BigDecimal amount = 1.0
                sendBitcoinAndWait(address, 1.0, it+1, "#addressesTable", amount)
                funds += amount
            }
            String lastReceivingAddress = lookup("#receivingAddress").queryAs(TextField).text

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

            TableView<AddressRow> addressesTable = lookup("#addressesTable").queryAs(TableView)
            def addressesTableSize = addressesTable.items.size()
            def firstRowAddressesTableBalance = addressesTable.items[0].balance

            clickOn("#transactionsTab")
            TableView<TransactionRow> transactionsTable = lookup("#transactionsTable").queryAs(TableView)
            def transactionTableSize = transactionsTable.items.size()
            def firstRowTransactionTableBalance = transactionsTable.items[0].balance
            String totalBalanceText = lookup("#totalBalance").queryAs(Label).getText()

            loadWallet(walletName)
            TableView<TransactionRow> transactionsTableAfterLoad = lookup("#transactionsTable").queryAs(TableView)
            clickOn("#addressesTab")
            TableView<AddressRow> addressesTableAfterLoad = lookup("#addressesTable").queryAs(TableView)
            clickOn("Receive")
            String lastReceivingAddressAfterLoad = lookup("#receivingAddress").queryAs(TextField).text
            String labelTextAfterLoad = lookup("#totalBalance").queryAs(Label).text
        then:
            lastReceivingAddress == lastReceivingAddressAfterLoad
            addressesTableSize == 1
            firstRowAddressesTableBalance == changeAmount
            addressesTableAfterLoad.items.size() == addressesTableSize
            addressesTableAfterLoad.items[0].balance == firstRowAddressesTableBalance
            amountToSendLabel == BitcoinFormatter.format(new BigDecimal(amountToSend))
            totalFeeLabel == totalFee
            totalLabel == totalSpent
            feeRateLabel == feeRate
            addressToSendLabel == nodeAddress
            transactionTableSize == previousUtxosNumber + 1
            firstRowTransactionTableBalance == "-".concat(totalSpent)
            transactionsTableAfterLoad.items.size() == transactionTableSize
            transactionsTableAfterLoad.items[0].balance == firstRowTransactionTableBalance
            totalBalanceText == "Total Balance: $changeAmount BTC (confirmed: ${BitcoinFormatter.format(funds)}, unconfirmed: ${"-".concat(totalSpent)})"
            totalBalanceText == labelTextAfterLoad
        where:
            previousUtxosNumber | amountToSend | totalFee       | totalSpent    | changeAmount | feeRate
            1                   | "0.5"        | "0.00002679"   | "0.50002679"  | "0.49997321" | "0.0002 BTC/kvByte"
            2                   | "1.5"        | "0.00003971"   | "1.50003971"  | "0.49996029" | "0.0002 BTC/kvByte"
    }

    def "should send bitcoin 2 times"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write("My Test Wallet 10")
            clickOn("Create")
            clickOn("OK")
            clickOn("Receive")
            waitLoadWallet()

            BigDecimal funds = 0
            IntStream.range(0, previousUtxosNumber).forEach{
                String address = lookup("#receivingAddress").queryAs(TextField).text
                BigDecimal amount = 1.0
                sendBitcoinAndWait(address, 1.0, 1, "#addressesTable", amount)
                funds += amount
            }

            String nodeAddress = nodeGetNewAddressClient.getNewAddress(TESTWALLET, "bech32")
            nodeGenerateToAddressClient.generateToAddress(TESTWALLET, 1, nodeAddress)
            clickOn("#sendTab")
            sendBitcoin(nodeAddress, amountToSend)
            clickOn("OK")
            nodeGenerateToAddressClient.generateToAddress(TESTWALLET, 1, nodeAddress)
            clickOn("#sendTab")
            sleep(TIMEOUT, SECONDS)
            TableView<AddressRow> addressesTable = lookup("#addressesTable").queryAs(TableView)
            String firstChangeAddress = addressesTable.items.first().address
            sendBitcoin(nodeAddress, amountToSend)

            String amountToSendLabel = lookup("#amountToSendDialog").queryAs(Label).text
            String totalFeeLabel = lookup("#totalFees").queryAs(Label).text
            String totalLabel = lookup("#total").queryAs(Label).text
            String feeRateLabel = lookup("#feeRate").queryAs(Label).text
            String addressToSendLabel = lookup("#addressToSendDialog").queryAs(Label).text

            clickOn("OK")
            sleep(TIMEOUT, SECONDS)

            addressesTable = lookup("#addressesTable").queryAs(TableView)
            String secondChangeAddress = addressesTable.items.first().address

            clickOn("#transactionsTab")
            TableView<TransactionRow> transactionsTable = lookup("#transactionsTable").queryAs(TableView)
            String totalBalanceText = lookup("#totalBalance").queryAs(Label).getText()
        then:
            firstChangeAddress != secondChangeAddress
            addressesTable.items.size() == 1
            addressesTable.items[0].balance == changeAmount
            amountToSendLabel == BitcoinFormatter.format(new BigDecimal(amountToSend))
            totalFeeLabel == totalFee
            totalLabel == totalSpent
            feeRateLabel == feeRate
            addressToSendLabel == nodeAddress
            transactionsTable.items.size() == previousUtxosNumber + 2
            transactionsTable.items[0].balance == "-".concat(totalSpent)
            totalBalanceText == "Total Balance: $changeAmount BTC (confirmed: ${BitcoinFormatter.format(funds - new BigDecimal(totalSpent))}, unconfirmed: ${"-".concat(totalSpent)})"
        where:
            previousUtxosNumber | amountToSend  | totalFee       | totalSpent    | changeAmount | feeRate
            1                   | "0.25"        | "0.00002679"   | "0.25002679"  | "0.49994642" | "0.0002 BTC/kvByte"
    }

    def "should not send bitcoin with wrong password"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write("My Test Wallet 11")
            clickOn("#password")
            String password = "mytestpassword"
            write(password)
            clickOn("Create")
            clickOn("OK")
            clickOn("Receive")
            waitLoadWallet()

            BigDecimal funds = 0
            IntStream.range(0, previousUtxosNumber).forEach{
                String address = lookup("#receivingAddress").queryAs(TextField).text
                BigDecimal amount = 1.0
                sendBitcoinAndWait(address, 1.0, 1, "#addressesTable", amount)
                funds += amount
            }
            String formattedFunds = BitcoinFormatter.format(funds)

            String nodeAddress = nodeGetNewAddressClient.getNewAddress(TESTWALLET, "bech32")
            nodeGenerateToAddressClient.generateToAddress(TESTWALLET, 1, nodeAddress)
            clickOn("#sendTab")
            sendBitcoin(nodeAddress, amountToSend)

            String amountToSendLabel = lookup("#amountToSendDialog").queryAs(Label).text
            String totalFeeLabel = lookup("#totalFees").queryAs(Label).text
            String totalLabel = lookup("#total").queryAs(Label).text
            String feeRateLabel = lookup("#feeRate").queryAs(Label).text
            String addressToSendLabel = lookup("#addressToSendDialog").queryAs(Label).text

            clickOn("#walletPassword")
            write("wrong password")

            clickOn("OK")
            String errorMessage = "Could not send transaction: wrong password."
            NodeQuery nodeQuery = lookup(errorMessage)
            clickOn("OK")

            TableView<AddressRow> addressesTable = lookup("#addressesTable").queryAs(TableView)

            clickOn("#transactionsTab")
            TableView<TransactionRow> transactionsTable = lookup("#transactionsTable").queryAs(TableView)
            String totalBalanceText = lookup("#totalBalance").queryAs(Label).getText()
        then:
            nodeQuery.queryLabeled().getText() == errorMessage
            addressesTable.items.size() == 1
            addressesTable.items[0].balance == formattedFunds
            amountToSendLabel == BitcoinFormatter.format(new BigDecimal(amountToSend))
            totalFeeLabel == totalFee
            totalLabel == totalSpent
            feeRateLabel == feeRate
            addressToSendLabel == nodeAddress
            transactionsTable.items.size() == 1
            transactionsTable.items[0].balance == formattedFunds
            totalBalanceText == "Total Balance: $formattedFunds BTC (confirmed: $formattedFunds, unconfirmed: 0.00000000)"
        where:
            previousUtxosNumber | amountToSend | totalFee       | totalSpent    | changeAmount | feeRate
            1                   | "0.5"        | "0.00002679"   | "0.50002679"  | "0.49997321" | "0.0002 BTC/kvByte"
    }

    def "should not send bitcoin without funds greater than amount + fee"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write("My Test Wallet 12")
            clickOn("Create")
            clickOn("OK")
            clickOn("Receive")
            waitLoadWallet()

            BigDecimal funds = 0
            IntStream.range(0, previousUtxosNumber).forEach{
                String address = lookup("#receivingAddress").queryAs(TextField).text
                sendBitcoinAndWait(address, previousAmount, 1, "#addressesTable", previousAmount)
                funds += previousAmount
            }

            String nodeAddress = nodeGetNewAddressClient.getNewAddress(TESTWALLET, "bech32")
            nodeGenerateToAddressClient.generateToAddress(TESTWALLET, 1, nodeAddress)
            clickOn("#sendTab")
            sendBitcoin(nodeAddress, amountToSend, false)
            String errorMessage = "Could not send transaction: not enough funds."
            NodeQuery nodeQuery = lookup(errorMessage)
            clickOn("OK")
        then:
            nodeQuery.queryLabeled().getText() == errorMessage
        where:
            previousUtxosNumber | amountToSend | previousAmount
            1                   | "0.5"        | 0.1
            1                   | "0.5"        | 0.50002089
    }

    def "should not send bitcoin without any funds"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write("My Test Wallet 13")
            clickOn("Create")
            clickOn("OK")
            clickOn("Receive")
            waitLoadWallet()

            String nodeAddress = nodeGetNewAddressClient.getNewAddress(TESTWALLET, "bech32")
            nodeGenerateToAddressClient.generateToAddress(TESTWALLET, 1, nodeAddress)
            clickOn("#sendTab")
            sendBitcoin(nodeAddress, amountToSend, false)
            String errorMessage = "Could not send transaction: not enough funds."
            NodeQuery nodeQuery = lookup(errorMessage)
            clickOn("OK")
        then:
            nodeQuery.queryLabeled().getText() == errorMessage
        where:
            amountToSend | _
            "0.5"        | _
    }

    def "should not send dust bitcoin"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write("My Test Wallet 18")
            clickOn("Create")
            clickOn("OK")
            clickOn("Receive")
            waitLoadWallet()

            BigDecimal funds = 0
            IntStream.range(0, previousUtxosNumber).forEach{
                String address = lookup("#receivingAddress").queryAs(TextField).text
                sendBitcoinAndWait(address, previousAmount, 1, "#addressesTable", previousAmount)
                funds += previousAmount
            }

            String nodeAddress = nodeGetNewAddressClient.getNewAddress(TESTWALLET, "bech32")
            nodeGenerateToAddressClient.generateToAddress(TESTWALLET, 1, nodeAddress)
            clickOn("#sendTab")
            sendBitcoin(nodeAddress, amountToSend)
            clickOn("OK")
            String errorMessage = "Could not send transaction: amount to send is dust."
            NodeQuery nodeQuery = lookup(errorMessage)
            clickOn("OK")
        then:
            nodeQuery.queryLabeled().getText() == errorMessage
        where:
            previousUtxosNumber | amountToSend | previousAmount
            1                   | "0.00000293" | 0.1
    }

    def "should not send bitcoin to invalid address"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write("My Test Wallet 34")
            clickOn("Create")
            clickOn("OK")
            clickOn("Receive")
            waitLoadWallet()

            BigDecimal funds = 0
            def previousAmount = 0.1
            IntStream.range(0, 1).forEach{
                String address = lookup("#receivingAddress").queryAs(TextField).text
                sendBitcoinAndWait(address, previousAmount, 1, "#addressesTable", previousAmount)
                funds += previousAmount
            }

            String nodeAddress = nodeGetNewAddressClient.getNewAddress(TESTWALLET, "bech32")
            nodeGenerateToAddressClient.generateToAddress(TESTWALLET, 1, nodeAddress)
            clickOn("#sendTab")
            sendBitcoin(addressToSend, "0.01", false)
            String errorMessage = "Could not send transaction: invalid address."
            NodeQuery nodeQuery = lookup(errorMessage)
            clickOn("OK")
        then:
            nodeQuery.queryLabeled().getText() == errorMessage
        where:
            addressToSend                                                    | _
            "asdf"                                                           | _
            "bcrtasdf"                                                       | _
            "bcrt1q3d5nn9qw9s44cr6g6mh75m0cf4tr7prsfrm5c7"                   | _
            "mu27DxrNovbD24uZJJXHnPxEDJkAZBCYN7"                             | _
            "2N2JnaoXzjcMYPWbofDQXT2wxy2ecUHKMgg"                            | _
            "bc1p0xlxvlhemja6c4dqv22uapctqupfhlxm9h8z3k2e72q4k9hcz7vqzk5jj0" | _
            "1EW9vumPzu9xExRwajYuxUjuMK9TbC8bks"                             | _
            "3AkaX4by89rCBiyFz5neq5xhkgSSfzEtfx"                             | _
    }
}
