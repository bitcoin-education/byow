package com.byow.wallet.byow.gui

import com.byow.wallet.byow.api.services.NodeEstimateFeeService
import com.byow.wallet.byow.api.services.TransactionSignerService
import com.byow.wallet.byow.domains.ExtendedPubkey
import com.byow.wallet.byow.domains.UnsignedTransactionPayload
import com.byow.wallet.byow.observables.AddressRow
import com.byow.wallet.byow.observables.TransactionRow
import com.byow.wallet.byow.utils.BitcoinFormatter
import com.byow.wallet.byow.utils.ExtendedPubkeysSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import javafx.scene.control.Label
import javafx.scene.control.TableView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.testfx.service.query.NodeQuery

import java.util.stream.IntStream

import static java.util.concurrent.TimeUnit.SECONDS
import static org.mockito.Mockito.when

class SendBitcoinWatchOnlyTest extends GuiTest {
    @MockBean
    NodeEstimateFeeService nodeEstimateFeeService

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    TransactionSignerService transactionSignerService

    def setup() {
        loadWalletAndAddBalance()
        when(nodeEstimateFeeService.estimate()).thenReturn(0.0002)
    }

    def "should send bitcoin from watch only wallet"() {
        given:
            def walletName = "My Test Wallet 40"
            def mnemonicSeed = mnemonicSeedService.create()
            List<ExtendedPubkey> extendedPubkeys = createExtendedPubkeysService.create(mnemonicSeed, "")
            def extendedPubKeysJson = ExtendedPubkeysSerializer.serialize(extendedPubkeys)
            createWatchOnlyWallet(walletName, extendedPubKeysJson)
            loadWallet(walletName)
            BigDecimal funds = 0
            IntStream.range(0, previousUtxosNumber).forEach{
                String address = lookup("#receivingAddress").queryAs(TextField).text
                BigDecimal amount = 1.0
                sendBitcoinAndWait(address, 1.0, it+1, "#addressesTable", amount)
                funds += amount
            }

            String nodeAddress = nodeGetNewAddressClient.getNewAddress(TESTWALLET, "bech32")
            nodeGenerateToAddressClient.generateToAddress(TESTWALLET, 1, nodeAddress)
        when:
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
            String unsignedTransactionJsonTextArea = lookup("#unsignedTransactionJsonTextArea").queryAs(TextArea).text
            UnsignedTransactionPayload unsignedTransactionPayload = objectMapper.readValue(unsignedTransactionJsonTextArea, UnsignedTransactionPayload.class)
            transactionSignerService.sign(unsignedTransactionPayload.transaction(), mnemonicSeed, "", unsignedTransactionPayload.utxos())
            clickOn("#signedTransactionTextArea")
            write(unsignedTransactionPayload.transaction().serialize())

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
        then:
            addressesTableSize == 1
            firstRowAddressesTableBalance == changeAmount
            amountToSendLabel == BitcoinFormatter.format(new BigDecimal(amountToSend))
            totalFeeLabel == totalFee
            totalLabel == totalSpent
            feeRateLabel == feeRate
            addressToSendLabel == nodeAddress
            transactionTableSize == previousUtxosNumber + 1
            firstRowTransactionTableBalance == "-".concat(totalSpent)
            totalBalanceText == "Total Balance: $changeAmount BTC (confirmed: ${BitcoinFormatter.format(funds)}, unconfirmed: ${"-".concat(totalSpent)})"
        where:
            previousUtxosNumber | amountToSend | totalFee       | totalSpent    | changeAmount | feeRate
            1                   | "0.5"        | "0.00002679"   | "0.50002679"  | "0.49997321" | "0.0002 BTC/kvByte"
            2                   | "1.5"        | "0.00003971"   | "1.50003971"  | "0.49996029" | "0.0002 BTC/kvByte"
    }

    def "should not send bitcoin from watch only wallet if sent transaction is different than expected"() {
        given:
            def walletName = "My Test Wallet 43"
            def mnemonicSeed = mnemonicSeedService.create()
            List<ExtendedPubkey> extendedPubkeys = createExtendedPubkeysService.create(mnemonicSeed, "")
            def extendedPubKeysJson = ExtendedPubkeysSerializer.serialize(extendedPubkeys)
            createWatchOnlyWallet(walletName, extendedPubKeysJson)
            loadWallet(walletName)
            BigDecimal funds = 0
            IntStream.range(0, previousUtxosNumber).forEach{
                String address = lookup("#receivingAddress").queryAs(TextField).text
                BigDecimal amount = 1.0
                sendBitcoinAndWait(address, 1.0, it+1, "#addressesTable", amount)
                funds += amount
            }

            String nodeAddress = nodeGetNewAddressClient.getNewAddress(TESTWALLET, "bech32")
            nodeGenerateToAddressClient.generateToAddress(TESTWALLET, 1, nodeAddress)
        when:
            clickOn("#sendTab")
            clickOn("#amountToSend")
            write(amountToSend)
            clickOn("#addressToSend")
            write(nodeAddress)
            clickOn("#send")
            waitForDialog()

            clickOn("#signedTransactionTextArea")
            def unrelatedTransactionHex = "0100000000010157ae075755df98e66bc0b1b9a9d0aa06741edae33a4abef4aecb13f0437b50c10100000000ffffffff0280f0fa020000000016001457047bf319eff2b8ce4ec4d106e9330523b0805b09e6fa0200000000160014025ec31a3614e7b974d089897fc9b689e64b7b3602483045022100ea245fd65b519f37809275723d9f5c678bc71bc86ea7704b56d6769d1f11a5b202204a8d7ac520f13228c43ddeb9fabc70d6fa10cbae3a4588eb98d4ca18c4126200012103929770a61ec8a5910e147c18fda5e78e8a832d92d3098300ac5468937e43075600000000"
            write(unrelatedTransactionHex)
            clickOn("OK")

            String errorMessage = "Could not send transaction: unexpected transaction content."
            NodeQuery nodeQuery = lookup(errorMessage)
            clickOn("#alertOk")
        then:
            nodeQuery.queryLabeled().text == errorMessage
        where:
            previousUtxosNumber | amountToSend | totalFee       | totalSpent    | changeAmount | feeRate
            1                   | "0.5"        | "0.00002679"   | "0.50002679"  | "0.49997321" | "0.0002 BTC/kvByte"
    }

    def "should not send bitcoin from watch only wallet if sent transaction has invalid format"() {
        given:
            def walletName = "My Test Wallet 44"
            def mnemonicSeed = mnemonicSeedService.create()
            List<ExtendedPubkey> extendedPubkeys = createExtendedPubkeysService.create(mnemonicSeed, "")
            def extendedPubKeysJson = ExtendedPubkeysSerializer.serialize(extendedPubkeys)
            createWatchOnlyWallet(walletName, extendedPubKeysJson)
            loadWallet(walletName)
            BigDecimal funds = 0
            IntStream.range(0, previousUtxosNumber).forEach{
                String address = lookup("#receivingAddress").queryAs(TextField).text
                BigDecimal amount = 1.0
                sendBitcoinAndWait(address, 1.0, it+1, "#addressesTable", amount)
                funds += amount
            }

            String nodeAddress = nodeGetNewAddressClient.getNewAddress(TESTWALLET, "bech32")
            nodeGenerateToAddressClient.generateToAddress(TESTWALLET, 1, nodeAddress)
            when:
            clickOn("#sendTab")
            clickOn("#amountToSend")
            write(amountToSend)
            clickOn("#addressToSend")
            write(nodeAddress)
            clickOn("#send")
            waitForDialog()

            clickOn("#signedTransactionTextArea")
            def unrelatedTransactionHex = "asdf"
            write(unrelatedTransactionHex)
            clickOn("OK")

            String errorMessage = "Could not send transaction: unexpected transaction content."
            NodeQuery nodeQuery = lookup(errorMessage)
            clickOn("#alertOk")
        then:
            nodeQuery.queryLabeled().text == errorMessage
        where:
            previousUtxosNumber | amountToSend | totalFee       | totalSpent    | changeAmount | feeRate
            1                   | "0.5"        | "0.00002679"   | "0.50002679"  | "0.49997321" | "0.0002 BTC/kvByte"
    }

    def "should not send bitcoin from watch only wallet if sent transaction is invalidated by node"() {
        given:
            def walletName = "My Test Wallet 45"
            def mnemonicSeed = mnemonicSeedService.create()
            List<ExtendedPubkey> extendedPubkeys = createExtendedPubkeysService.create(mnemonicSeed, "")
            def extendedPubKeysJson = ExtendedPubkeysSerializer.serialize(extendedPubkeys)
            createWatchOnlyWallet(walletName, extendedPubKeysJson)
            loadWallet(walletName)
            BigDecimal funds = 0
            IntStream.range(0, previousUtxosNumber).forEach{
                String address = lookup("#receivingAddress").queryAs(TextField).text
                BigDecimal amount = 1.0
                sendBitcoinAndWait(address, 1.0, it+1, "#addressesTable", amount)
                funds += amount
            }

            String nodeAddress = nodeGetNewAddressClient.getNewAddress(TESTWALLET, "bech32")
            nodeGenerateToAddressClient.generateToAddress(TESTWALLET, 1, nodeAddress)
            when:
            clickOn("#sendTab")
            clickOn("#amountToSend")
            write(amountToSend)
            clickOn("#addressToSend")
            write(nodeAddress)
            clickOn("#send")
            waitForDialog()

            String unsignedTransactionJsonTextArea = lookup("#unsignedTransactionJsonTextArea").queryAs(TextArea).text
            UnsignedTransactionPayload unsignedTransactionPayload = objectMapper.readValue(unsignedTransactionJsonTextArea, UnsignedTransactionPayload.class)
            clickOn("#signedTransactionTextArea")
            write(unsignedTransactionPayload.transaction().serialize())
            clickOn("OK")

            String errorMessage = "Could not send transaction: wrong signature."
            NodeQuery nodeQuery = lookup(errorMessage)
            clickOn("#alertOk")
        then:
            nodeQuery.queryLabeled().text == errorMessage
        where:
            previousUtxosNumber | amountToSend | totalFee       | totalSpent    | changeAmount | feeRate
            1                   | "0.5"        | "0.00002679"   | "0.50002679"  | "0.49997321" | "0.0002 BTC/kvByte"
    }
}
