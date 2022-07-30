package com.byow.wallet.byow.gui

import com.byow.wallet.byow.api.services.NodeEstimateFeeService
import com.byow.wallet.byow.observables.AddressRow
import com.byow.wallet.byow.observables.TransactionRow
import javafx.scene.control.Label
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import org.springframework.boot.test.mock.mockito.MockBean

import static org.mockito.Mockito.when

class LoadWalletTest extends GuiTest {
    @MockBean
    NodeEstimateFeeService nodeEstimateFeeService

    def setup() {
        loadWalletAndAddBalance()
        when(nodeEstimateFeeService.estimate()).thenReturn(0.0002)
    }

    def "should load wallet and receive bitcoin"() {
        given:
            def walletName = "My Test Wallet 27"
            def password = ""
            def mnemonicSeed = mnemonicSeedService.create()
            createWallet(walletName, password, mnemonicSeed)
        when:
            loadWallet(walletName)
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

    def "should load wallet with different password and receive bitcoin"() {
        given:
            def walletName = "My Test Wallet 28"
            def password = ""
            def mnemonicSeed = mnemonicSeedService.create()
            createWallet(walletName, password, mnemonicSeed)
        when:
            def differentPassword = "different password"
            loadWallet(walletName, differentPassword)
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
            addressIsValid(address, mnemonicSeed, 0, differentPassword)
            labelTextAfterLoad == "Total Balance: 0.00001000 BTC (confirmed: 0.00000000, unconfirmed: 0.00001000)"
    }
}