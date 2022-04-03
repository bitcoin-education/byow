package com.byow.wallet.byow.gui

import javafx.scene.control.TableView
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
            clickOn("OK")
            clickOn("Receive")
            String address = lookup("#receivingAddress").queryAs(TextField).text
            sleep(TIMEOUT, SECONDS)
            sendBitcoinAndWait(address, 1.0, 1, "#addressesTable", 1.0)
            String nodeAddress = nodeGetNewAddressClient.getNewAddress(TESTWALLET)
            nodeGenerateToAddressClient.generateToAddress(TESTWALLET, 1, address)
            clickOn("#sendTab")
            clickOn("#amountToSend")
            write("0.5")
            clickOn("#addressToSend")
            write(nodeAddress)
            clickOn("#send")
            sleep(TIMEOUT, SECONDS)
            clickOn("OK")
            clickOn("#transactionsTab")
            sleep(TIMEOUT, SECONDS)
            TableView tableView = lookup("#transactionsTable").queryAs(TableView)
        then:
            tableView.items.size() == 2
    }
}
