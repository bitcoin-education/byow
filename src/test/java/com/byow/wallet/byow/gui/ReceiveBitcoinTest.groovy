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
    public static final int TIMEOUT = 10

    public static final String TEST_WALLET_NAME = "testwallet"

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
        nodeLoadOrCreateWalletService.loadOrCreateWallet(TEST_WALLET_NAME)
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
            nodeSendToAddressClient.sendToAddress(TEST_WALLET_NAME, address, 1.0)
            waitFor(TIMEOUT, SECONDS, {
                TableView tableView = lookup("#addressesTable").queryAs(TableView)
                return tableView.items.size() == 1
            })
            TableView tableView = lookup("#addressesTable").queryAs(TableView)
        then:
            tableView.items.size() == 1
        }

    private void createBalanceIfNecessary() {
        double balance = nodeGetBalanceClient.get(TEST_WALLET_NAME)
        if (balance < 100) {
            String address = nodeGetNewAddressClient.getNewAddress(TEST_WALLET_NAME)
            nodeGenerateToAddressClient.generateToAddress(TEST_WALLET_NAME, 1000, address)
        }
    }
}
