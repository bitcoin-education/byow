package com.byow.wallet.byow.gui

import com.byow.wallet.byow.api.services.NodeEstimateFeeService
import com.byow.wallet.byow.domains.ExtendedPubkey
import com.byow.wallet.byow.utils.ExtendedPubkeysSerializer
import javafx.scene.control.Label
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import org.springframework.boot.test.mock.mockito.MockBean
import org.testfx.service.query.NodeQuery

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

    def "should load watch only wallet and receive bitcoin"() {
        given:
            def walletName = "My Test Wallet 36"
            def mnemonicSeed = mnemonicSeedService.create()
            List<ExtendedPubkey> extendedPubkeys = createExtendedPubkeysService.create(mnemonicSeed, "")
            def extendedPubKeysJson = ExtendedPubkeysSerializer.serialize(extendedPubkeys)
            createWatchOnlyWallet(walletName, extendedPubKeysJson)
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

    def "should load watch only wallet with password and receive bitcoin"() {
        given:
            def walletName = "My Test Wallet 41"
            def mnemonicSeed = mnemonicSeedService.create()
            List<ExtendedPubkey> extendedPubkeys = createExtendedPubkeysService.create(mnemonicSeed, "")
            def extendedPubKeysJson = ExtendedPubkeysSerializer.serialize(extendedPubkeys)
            createWatchOnlyWallet(walletName, extendedPubKeysJson, "password")
        when:
            loadWallet(walletName, "password")
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

    def "should not load watch only wallet with wrong password"() {
        given:
            def walletName = "My Test Wallet 42"
            def mnemonicSeed = mnemonicSeedService.create()
            List<ExtendedPubkey> extendedPubkeys = createExtendedPubkeysService.create(mnemonicSeed, "")
            def extendedPubKeysJson = ExtendedPubkeysSerializer.serialize(extendedPubkeys)
            createWatchOnlyWallet(walletName, extendedPubKeysJson, "password")
        when:
            loadWallet(walletName, "wrong password")
            String errorMessage = "Could not load wallet: the wallet password is wrong."
            NodeQuery nodeQuery = lookup(errorMessage)
            clickOn("#alertOk")

        then:
            nodeQuery.queryLabeled().text == errorMessage
    }
}
