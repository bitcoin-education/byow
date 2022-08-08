package com.byow.wallet.byow.gui

import com.byow.wallet.byow.api.services.NodeEstimateFeeService
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
            TableView addressesTableView = lookup("#addressesTable").queryAs(TableView)
            clickOn("Transactions")
            TableView transactionsTableView = lookup("#transactionsTable").queryAs(TableView)
            String labelText = lookup("#totalBalance").queryAs(Label).getText()
        then:
            addressesTableView.items.size() == 1
            transactionsTableView.items.size() == 1
            addressIsValid(address, mnemonicSeed, 0)
            labelText == "Total Balance: 0.00001000 BTC (confirmed: 0.00000000, unconfirmed: 0.00001000)"
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
            TableView addressesTableView = lookup("#addressesTable").queryAs(TableView)
            clickOn("Transactions")
            TableView transactionsTableView = lookup("#transactionsTable").queryAs(TableView)
            String labelText = lookup("#totalBalance").queryAs(Label).getText()
        then:
            addressesTableView.items.size() == 1
            transactionsTableView.items.size() == 1
            addressIsValid(address, mnemonicSeed, 0, differentPassword)
            labelText == "Total Balance: 0.00001000 BTC (confirmed: 0.00000000, unconfirmed: 0.00001000)"
    }
}
