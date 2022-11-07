package com.byow.wallet.byow.gui

import com.byow.wallet.byow.api.services.NodeEstimateFeeService
import com.byow.wallet.byow.observables.AddressRow
import com.byow.wallet.byow.observables.TransactionRow
import javafx.scene.control.Label
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import org.springframework.boot.test.mock.mockito.MockBean
import org.testfx.service.query.NodeQuery

import java.util.concurrent.TimeUnit

import static org.mockito.Mockito.when

class ImportWalletTest extends GuiTest {
    @MockBean
    NodeEstimateFeeService nodeEstimateFeeService

    def setup() {
        loadWalletAndAddBalance()
        when(nodeEstimateFeeService.estimate()).thenReturn(0.0002)
    }

    def "should import wallet and receive bitcoin"() {
        when:
            def walletName = "My Test Wallet 29"
            def mnemonicSeed = mnemonicSeedService.create()
            importWallet(walletName, mnemonicSeed)
            String address = lookup("#receivingAddress").queryAs(TextField).text
            sendBitcoinAndWait(address, 0.00001, 1, "#addressesTable", 0.00001)

            clickOn("#addressesTab")
            TableView<AddressRow> addressesTable = lookup("#addressesTable").queryAs(TableView)

            clickOn("Transactions")
            TableView<TransactionRow> transactionsTable = lookup("#transactionsTable").queryAs(TableView)

            String labelText = lookup("#totalBalance").queryAs(Label).text

            def addressesTableSize = addressesTable.items.size()
            def transactionTableSize = transactionsTable.items.size()
        then:
            addressesTableSize == 1
            transactionTableSize == 1
            addressIsValid(address, mnemonicSeed, 0)
            labelText == "Total Balance: 0.00001000 BTC (confirmed: 0.00000000, unconfirmed: 0.00001000)"
    }

    def "should import used wallet and receive block"() {
        given:
            def walletName = "My Test Wallet 30"
            def password = ""
            def mnemonicSeed = mnemonicSeedService.create()
            def wallet = createWallet(walletName, password, mnemonicSeed)
            def address = wallet.firstAddress
            nodeSendToAddressClient.sendToAddress(TESTWALLET, address, 1)
        when:
            def importWalletName = "My Test Wallet 31"
            importWallet(importWalletName, mnemonicSeed, password)
            String nodeAddress = nodeGetNewAddressClient.getNewAddress(TESTWALLET, "bech32")
            nodeGenerateToAddressClient.generateToAddress(TESTWALLET, 1, nodeAddress)
            sleep(TIMEOUT, TimeUnit.SECONDS)

            clickOn("#addressesTab")
            TableView<AddressRow> addressesTable = lookup("#addressesTable").queryAs(TableView)

            clickOn("Transactions")
            TableView<TransactionRow> transactionsTable = lookup("#transactionsTable").queryAs(TableView)

            String labelText = lookup("#totalBalance").queryAs(Label).text

            def addressesTableSize = addressesTable.items.size()
            def transactionTableSize = transactionsTable.items.size()
        then:
            addressesTableSize == 1
            transactionTableSize == 1
            addressIsValid(address, mnemonicSeed, 0)
            labelText == "Total Balance: 1.00000000 BTC (confirmed: 1.00000000, unconfirmed: 0.00000000)"
    }

    def "should not import wallet with repeated name"() {
        given:
            def walletName = "My Test Wallet 33"
            def password = ""
            def mnemonicSeed = mnemonicSeedService.create()
            createWallet(walletName, password, mnemonicSeed)
        when:
            clickOn("Import")
            clickOn("Wallet")
            clickOn("#walletName")
            write(walletName)
            clickOn("#walletPassword")
            write(password)
            clickOn("#mnemonicSeed")
            write(mnemonicSeed)
            clickOn("OK")
            sleep(5, TimeUnit.SECONDS)

            String errorMessage = "Could not create wallet: the wallet name already exists."
            NodeQuery nodeQuery = lookup(errorMessage)
            clickOn("#alertOk")
        then:
            nodeQuery.queryLabeled().text == errorMessage
    }
}
