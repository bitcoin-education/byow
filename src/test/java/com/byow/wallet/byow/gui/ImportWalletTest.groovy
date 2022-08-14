package com.byow.wallet.byow.gui

import com.byow.wallet.byow.api.services.NodeEstimateFeeService
import com.byow.wallet.byow.observables.AddressRow
import com.byow.wallet.byow.observables.TransactionRow
import javafx.scene.control.Label
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import org.springframework.boot.test.mock.mockito.MockBean

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
            TableView<AddressRow> addressesTableAfterLoad = lookup("#addressesTable").queryAs(TableView)

            clickOn("Transactions")
            TableView<TransactionRow> transactionsTableAfterLoad = lookup("#transactionsTable").queryAs(TableView)

            String labelTextAfterLoad = lookup("#totalBalance").queryAs(Label).getText()

            def addressesTableSize = addressesTableAfterLoad.items.size()
            def transactionsTableSize = transactionsTableAfterLoad.items.size()
        then:
            addressesTableSize == 1
            transactionsTableSize == 1
            addressIsValid(address, mnemonicSeed, 0)
            labelTextAfterLoad == "Total Balance: 0.00001000 BTC (confirmed: 0.00000000, unconfirmed: 0.00001000)"
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
            TableView<AddressRow> addressesTableAfterLoad = lookup("#addressesTable").queryAs(TableView)

            clickOn("Transactions")
            TableView<TransactionRow> transactionsTableAfterLoad = lookup("#transactionsTable").queryAs(TableView)

            String labelTextAfterLoad = lookup("#totalBalance").queryAs(Label).getText()

            def addressesTableSize = addressesTableAfterLoad.items.size()
            def transactionsTableSize = transactionsTableAfterLoad.items.size()
        then:
            addressesTableSize == 1
            transactionsTableSize == 1
            addressIsValid(address, mnemonicSeed, 0)
            labelTextAfterLoad == "Total Balance: 1.00000000 BTC (confirmed: 1.00000000, unconfirmed: 0.00000000)"
    }
}