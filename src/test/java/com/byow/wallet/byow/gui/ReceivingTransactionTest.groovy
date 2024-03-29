package com.byow.wallet.byow.gui

import javafx.scene.control.TableView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField

class ReceivingTransactionTest extends GuiTest {
    def setup() {
        loadWalletAndAddBalance()
    }

    def "should receive transaction"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write("My Test Wallet 7")
            clickOn("Create")
            def mnemonicSeed = lookup("#mnemonicSeed").queryAs(TextArea).text
            clickOn("OK")
            clickOn("Receive")
            waitLoadWallet()
            String address = lookup("#receivingAddress").queryAs(TextField).text
            clickOn("#transactionsTab")
            sendBitcoinAndWait(address, 1.0, 1, "#transactionsTable")
            TableView tableView = lookup("#transactionsTable").queryAs(TableView)
        then:
            tableView.items.size() == 1
    }
}
