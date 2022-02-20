package com.byow.wallet.byow.gui

import com.byow.wallet.byow.api.services.node.NodeLoadOrCreateWalletService
import com.byow.wallet.byow.api.services.node.client.NodeGenerateToAddressClient
import com.byow.wallet.byow.api.services.node.client.NodeGetBalanceClient
import com.byow.wallet.byow.api.services.node.client.NodeGetNewAddressClient
import com.byow.wallet.byow.api.services.node.client.NodeSendToAddressClient
import javafx.scene.control.TableView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import org.springframework.beans.factory.annotation.Autowired

import static java.util.concurrent.TimeUnit.SECONDS
import static org.testfx.util.WaitForAsyncUtils.waitFor

class ReceiveBitcoinTest extends GuiTest {

    public static final String TESTWALLET = "testwallet"

    public static final int TIMEOUT = 10

    @Autowired
    private NodeSendToAddressClient nodeSendToAddressClient

    @Autowired
    private NodeLoadOrCreateWalletService nodeLoadOrCreateWalletService

    @Autowired
    private NodeGetBalanceClient nodeGetBalanceClient

    @Autowired
    private NodeGetNewAddressClient nodeGetNewAddressClient

    @Autowired
    private NodeGenerateToAddressClient nodeGenerateToAddressClient

    def setup() {
        nodeLoadOrCreateWalletService.loadOrCreateWallet(TESTWALLET)
        createBalanceIfNecessary()
    }

    def "should receive bitcoin"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write("My Test Wallet")
            clickOn("Create")
            def mnemonicSeed = lookup("#mnemonicSeed").queryAs(TextArea).text
            clickOn("OK")
            clickOn("Receive")
            String address = lookup("#receivingAddress").queryAs(TextField).text
            sleep(TIMEOUT, SECONDS)
            sendBitcoinAndWait(address)
            TableView tableView = lookup("#addressesTable").queryAs(TableView)
        then:
            tableView.items.size() == 1
    }

    def "should receive bitcoins in two transactions to the same address"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write("My Test Wallet 2")
            clickOn("Create")
            def mnemonicSeed = lookup("#mnemonicSeed").queryAs(TextArea).text
            clickOn("OK")
            clickOn("Receive")
            String address = lookup("#receivingAddress").queryAs(TextField).text
            sleep(TIMEOUT, SECONDS)
            sendBitcoinAndWait(address)
            sendBitcoinAndWait(address, "2.0")
            TableView tableView = lookup("#addressesTable").queryAs(TableView)
        then:
            tableView.items.size() == 1
    }

    def "should receive bitcoins in two transactions to different addresses"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write("My Test Wallet 3")
            clickOn("Create")
            def mnemonicSeed = lookup("#mnemonicSeed").queryAs(TextArea).text
            clickOn("OK")
            clickOn("Receive")
            String address = lookup("#receivingAddress").queryAs(TextField).text
            sleep(TIMEOUT, SECONDS)
            sendBitcoinAndWait(address)
            String nextAddress = lookup("#receivingAddress").queryAs(TextField).text
            sendBitcoinAndWait(nextAddress, "1.0", 2)
            TableView tableView = lookup("#addressesTable").queryAs(TableView)
        then:
            tableView.items.size() == 2
    }

    private void sendBitcoinAndWait(String address, String expectedTotalAmount = "1.0", int expectedTotalSize = 1) {
        nodeSendToAddressClient.sendToAddress(TESTWALLET, address, 1.0)
        waitFor(TIMEOUT, SECONDS, {
            TableView tableView = lookup("#addressesTable").queryAs(TableView)
            return tableView.items.size() == expectedTotalSize && tableView.items[expectedTotalSize - 1].balance == expectedTotalAmount
        })
    }

    private void createBalanceIfNecessary() {
        double balance = nodeGetBalanceClient.get(TESTWALLET)
        if (balance < 50) {
            String address = nodeGetNewAddressClient.getNewAddress(TESTWALLET)
            nodeGenerateToAddressClient.generateToAddress(TESTWALLET, 150, address)
        }
    }

}
