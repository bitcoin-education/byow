package com.byow.wallet.byow.gui

import javafx.scene.control.TableView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import spock.lang.Unroll

import static java.util.concurrent.TimeUnit.SECONDS
import static org.testfx.util.WaitForAsyncUtils.waitFor

class ReceivingBlockTest extends GuiTest {
    def setup() {
        loadWalletAndAddBalance()
    }

    @Unroll
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
            String address = lookup("#receivingAddress").queryAs(TextField).text
            sleep(TIMEOUT, SECONDS)
            sendBitcoinAndWait(address)
            mineBlocks(confirmations)
            TableView tableView = lookup("#addressesTable").queryAs(TableView)
        then:
            tableView.items.size() == 1
            tableView.items[0].confirmations == confirmations
            addressIsValid(address, mnemonicSeed, 0)
        where:
            confirmations | x
            1             | null
            2             | null
            3             | null
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
            String address = lookup("#receivingAddress").queryAs(TextField).text
            sleep(TIMEOUT, SECONDS)
            sendBitcoinAndWait(address)
            mineBlocks(confirmations)
            sendBitcoinAndWait(address)
            mineBlocks(confirmations - 1)
            TableView tableView = lookup("#addressesTable").queryAs(TableView)
        then:
            tableView.items.size() == 1
            tableView.items[0].confirmations == confirmations - 1
            addressIsValid(address, mnemonicSeed, 0)
            where:
            confirmations | x
            2             | null
            3             | null
    }

    void mineBlocks(int blocks) {
        String nodeAddress = nodeGetNewAddressClient.getNewAddress(TESTWALLET)
        nodeGenerateToAddressClient.generateToAddress(TESTWALLET, blocks, nodeAddress)
        waitFor(TIMEOUT, SECONDS, {
            TableView tableView = lookup("#addressesTable").queryAs(TableView)
            return tableView.items[0].confirmations == blocks
        })
    }
}
