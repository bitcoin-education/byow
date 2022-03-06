package com.byow.wallet.byow.gui

import javafx.scene.control.TableView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField

import static java.util.concurrent.TimeUnit.SECONDS

class ReceiveBitcoinTest extends GuiTest {

    def setup() {
        loadWalletAndAddBalance()
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
            TableView addressesTableView = lookup("#addressesTable").queryAs(TableView)
            clickOn("Transactions")
            TableView transactionsTableView = lookup("#transactionsTable").queryAs(TableView)
        then:
            addressesTableView.items.size() == 1
            transactionsTableView.items.size() == 1
            addressIsValid(address, mnemonicSeed, 0)
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
            TableView addressesTableView = lookup("#addressesTable").queryAs(TableView)
            clickOn("Transactions")
            TableView transactionsTableView = lookup("#transactionsTable").queryAs(TableView)
        then:
            addressesTableView.items.size() == 1
            transactionsTableView.items.size() == 2
            addressIsValid(address, mnemonicSeed, 0)
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
            TableView addressesTableView = lookup("#addressesTable").queryAs(TableView)
            clickOn("Transactions")
            TableView transactionsTableView = lookup("#transactionsTable").queryAs(TableView)
        then:
            addressesTableView.items.size() == 2
            transactionsTableView.items.size() == 2
            addressIsValid(address, mnemonicSeed, 0)
            addressIsValid(nextAddress, mnemonicSeed, 1)
    }

    def "should receive bitcoins in seven transactions to different addresses"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write("My Test Wallet 4")
            clickOn("Create")
            def mnemonicSeed = lookup("#mnemonicSeed").queryAs(TextArea).text
            clickOn("OK")
            clickOn("Receive")
            String firstAddress = lookup("#receivingAddress").queryAs(TextField).text
            sleep(TIMEOUT, SECONDS)
            sendBitcoinAndWait(firstAddress)
            String secondAddress = lookup("#receivingAddress").queryAs(TextField).text
            sendBitcoinAndWait(secondAddress, "1.0", 2)
            String thirdAddress = lookup("#receivingAddress").queryAs(TextField).text
            sendBitcoinAndWait(thirdAddress, "1.0", 3)
            sleep(TIMEOUT, SECONDS)
            String fourthAddress = lookup("#receivingAddress").queryAs(TextField).text
            sendBitcoinAndWait(fourthAddress, "1.0", 4)
            String fifthAddress = lookup("#receivingAddress").queryAs(TextField).text
            sendBitcoinAndWait(fifthAddress, "1.0", 5)
            String sixthAddress = lookup("#receivingAddress").queryAs(TextField).text
            sendBitcoinAndWait(sixthAddress, "1.0", 6)
            sleep(TIMEOUT, SECONDS)
            String seventhAddress = lookup("#receivingAddress").queryAs(TextField).text
            sendBitcoinAndWait(seventhAddress, "1.0", 7)
            TableView addressesTableView = lookup("#addressesTable").queryAs(TableView)
            clickOn("Transactions")
            TableView transactionsTableView = lookup("#transactionsTable").queryAs(TableView)
        then:
            addressesTableView.items.size() == 7
            transactionsTableView.items.size() == 7
            addressIsValid(firstAddress, mnemonicSeed, 0)
            addressIsValid(secondAddress, mnemonicSeed, 1)
            addressIsValid(thirdAddress, mnemonicSeed, 2)
            addressIsValid(fourthAddress, mnemonicSeed, 3)
            addressIsValid(fifthAddress, mnemonicSeed, 4)
            addressIsValid(sixthAddress, mnemonicSeed, 5)
            addressIsValid(seventhAddress, mnemonicSeed, 6)
    }

}
