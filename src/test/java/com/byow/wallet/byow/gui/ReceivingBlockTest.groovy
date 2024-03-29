package com.byow.wallet.byow.gui

import javafx.scene.control.Label
import javafx.scene.control.TableView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField

import static java.util.concurrent.TimeUnit.SECONDS
import static org.testfx.util.WaitForAsyncUtils.waitFor

class ReceivingBlockTest extends GuiTest {
    def setup() {
        loadWalletAndAddBalance()
    }

    def "should receive bitcoin and add #confirmations confirmations to received address"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write("My Test Wallet 5")
            clickOn("Create")
            def mnemonicSeed = lookup("#mnemonicSeed").queryAs(TextArea).text
            clickOn("OK")
            clickOn("Receive")
            waitLoadWallet()
            String address = lookup("#receivingAddress").queryAs(TextField).text
            sendBitcoinAndWait(address)
            mineBlocks(confirmations)
            TableView addressesTableView = lookup("#addressesTable").queryAs(TableView)
            clickOn("Transactions")
            TableView transactionsTableView = lookup("#transactionsTable").queryAs(TableView)
            String labelText = lookup("#totalBalance").queryAs(Label).getText()
        then:
            addressesTableView.items.size() == 1
            addressesTableView.items[0].confirmations == confirmations
            addressIsValid(address, mnemonicSeed, 0)
            transactionsTableView.items.size() == 1
            transactionsTableView.items[0].confirmations == confirmations
            labelText == "Total Balance: 1.00000000 BTC (confirmed: 1.00000000, unconfirmed: 0.00000000)"
        where:
            confirmations   |   x
            1               |   null
            2               |   null
            3               |   null
    }

    def "should receive bitcoin and consider the transaction with fewer confirmations as the address confirmations"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write("My Test Wallet 6")
            clickOn("Create")
            def mnemonicSeed = lookup("#mnemonicSeed").queryAs(TextArea).text
            clickOn("OK")
            clickOn("Receive")
            waitLoadWallet()
            String address = lookup("#receivingAddress").queryAs(TextField).text
            sendBitcoinAndWait(address)
            mineBlocks(confirmations)
            sendBitcoinAndWait(address)
            mineBlocks(confirmations - 1)
            int totalConfirmations = confirmations + confirmations - 1
            TableView addressesTableView = lookup("#addressesTable").queryAs(TableView)
            clickOn("Transactions")
            TableView transactionsTableView = lookup("#transactionsTable").queryAs(TableView)
            String labelText = lookup("#totalBalance").queryAs(Label).getText()
        then:
            addressesTableView.items.size() == 1
            addressesTableView.items[0].confirmations == confirmations - 1
            addressIsValid(address, mnemonicSeed, 0)
            transactionsTableView.items.size() == 2
            transactionsTableView.items[0].confirmations == confirmations - 1
            transactionsTableView.items[1].confirmations == totalConfirmations
            labelText == "Total Balance: 2.00000000 BTC (confirmed: 2.00000000, unconfirmed: 0.00000000)"
        where:
            confirmations   |   x
            2               |   null
            3               |   null
    }

    void mineBlocks(int blocks) {
        String nodeAddress = nodeGetNewAddressClient.getNewAddress(TESTWALLET, "bech32")
        nodeGenerateToAddressClient.generateToAddress(TESTWALLET, blocks, nodeAddress)
        waitFor(TIMEOUT, SECONDS, {
            TableView tableView = lookup("#addressesTable").queryAs(TableView)
            return tableView?.items?[0]?.confirmations == blocks
        })
    }
}
