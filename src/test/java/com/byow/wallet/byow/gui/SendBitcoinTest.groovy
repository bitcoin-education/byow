package com.byow.wallet.byow.gui

import javafx.scene.control.TableView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField

import static java.util.concurrent.TimeUnit.SECONDS

class SendBitcoinTest extends GuiTest {
    def setup() {
        loadWalletAndAddBalance()
    }

    def "should send bitcoin"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write("My Test Wallet 9")
            clickOn("Create")
            def mnemonicSeed = lookup("#mnemonicSeed").queryAs(TextArea).text
            clickOn("OK")
            clickOn("Receive")
            String address = lookup("#receivingAddress").queryAs(TextField).text
            sleep(TIMEOUT, SECONDS)
            sendBitcoinAndWait(address, 1.0, 1, "#addressesTable", 1.0)
            clickOn("#sendTab")
            clickOn("#amountToSend")
            write("0.5")
            clickOn("#addressToSend")
            String nodeAddress = nodeGetNewAddressClient.getNewAddress(TESTWALLET)
            write(nodeAddress)
            clickOn("#send")
            clickOn("OK")
            clickOn("#transactionsTab")
            TableView tableView = lookup("#transactionsTable").queryAs(TableView)
        then:
            tableView.items.size() == 2
    }
}
