package com.byow.wallet.byow.gui

import javafx.scene.control.Label
import javafx.scene.control.TableView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField

import static java.util.concurrent.TimeUnit.SECONDS

class ReceiveBitcoinNestedSegwitTest extends GuiTest {
    def setup() {
        loadWalletAndAddBalance()
    }

    def "should receive bitcoin in a nested segwit address"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write("My Test Wallet 19")
            clickOn("Create")
            def mnemonicSeed = lookup("#mnemonicSeed").queryAs(TextArea).text
            clickOn("OK")
            clickOn("Receive")
            String address = lookup("#nestedSegwitReceivingAddress").queryAs(TextField).text
            sleep(TIMEOUT, SECONDS)
            sendBitcoinAndWait(address, 0.00001, 1, "#addressesTable", 0.00001)
            TableView addressesTableView = lookup("#addressesTable").queryAs(TableView)
            clickOn("Transactions")
            TableView transactionsTableView = lookup("#transactionsTable").queryAs(TableView)
            String labelText = lookup("#totalBalance").queryAs(Label).getText()
        then:
            addressesTableView.items.size() == 1
            transactionsTableView.items.size() == 1
            nestedSegwitAddressIsValid(address, mnemonicSeed, 0)
            labelText == "Total Balance: 0.00001000 BTC (confirmed: 0.00000000, unconfirmed: 0.00001000)"
    }

    def "should receive bitcoins in two transactions to the same nested segwit address"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write("My Test Wallet 20")
            clickOn("Create")
            def mnemonicSeed = lookup("#mnemonicSeed").queryAs(TextArea).text
            clickOn("OK")
            clickOn("Receive")
            String address = lookup("#nestedSegwitReceivingAddress").queryAs(TextField).text
            sleep(TIMEOUT, SECONDS)
            sendBitcoinAndWait(address)
            sendBitcoinAndWait(address, 2.0)
            TableView addressesTableView = lookup("#addressesTable").queryAs(TableView)
            clickOn("Transactions")
            TableView transactionsTableView = lookup("#transactionsTable").queryAs(TableView)
            String labelText = lookup("#totalBalance").queryAs(Label).getText()
        then:
            addressesTableView.items.size() == 1
            transactionsTableView.items.size() == 2
            nestedSegwitAddressIsValid(address, mnemonicSeed, 0)
            labelText == "Total Balance: 2.00000000 BTC (confirmed: 0.00000000, unconfirmed: 2.00000000)"
    }

    def "should receive bitcoins in two transactions to different nested segwit addresses"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write("My Test Wallet 21")
            clickOn("Create")
            def mnemonicSeed = lookup("#mnemonicSeed").queryAs(TextArea).text
            clickOn("OK")
            clickOn("Receive")
            String address = lookup("#nestedSegwitReceivingAddress").queryAs(TextField).text
            sleep(TIMEOUT, SECONDS)
            sendBitcoinAndWait(address)
            String nextAddress = lookup("#nestedSegwitReceivingAddress").queryAs(TextField).text
            sendBitcoinAndWait(nextAddress, 1.0, 2)
            TableView addressesTableView = lookup("#addressesTable").queryAs(TableView)
            clickOn("Transactions")
            TableView transactionsTableView = lookup("#transactionsTable").queryAs(TableView)
            String labelText = lookup("#totalBalance").queryAs(Label).getText()
        then:
            addressesTableView.items.size() == 2
            transactionsTableView.items.size() == 2
            nestedSegwitAddressIsValid(address, mnemonicSeed, 0)
            nestedSegwitAddressIsValid(nextAddress, mnemonicSeed, 1)
            labelText == "Total Balance: 2.00000000 BTC (confirmed: 0.00000000, unconfirmed: 2.00000000)"
    }

    def "should receive bitcoins in seven transactions to different nested segwit addresses"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write("My Test Wallet 22")
            clickOn("Create")
            def mnemonicSeed = lookup("#mnemonicSeed").queryAs(TextArea).text
            clickOn("OK")
            clickOn("Receive")
            String firstAddress = lookup("#nestedSegwitReceivingAddress").queryAs(TextField).text
            sleep(TIMEOUT, SECONDS)
            sendBitcoinAndWait(firstAddress)
            String secondAddress = lookup("#nestedSegwitReceivingAddress").queryAs(TextField).text
            sendBitcoinAndWait(secondAddress, 1.0, 2)
            String thirdAddress = lookup("#nestedSegwitReceivingAddress").queryAs(TextField).text
            sendBitcoinAndWait(thirdAddress, 1.0, 3)
            sleep(TIMEOUT, SECONDS)
            String fourthAddress = lookup("#nestedSegwitReceivingAddress").queryAs(TextField).text
            sendBitcoinAndWait(fourthAddress, 1.0, 4)
            String fifthAddress = lookup("#nestedSegwitReceivingAddress").queryAs(TextField).text
            sendBitcoinAndWait(fifthAddress, 1.0, 5)
            String sixthAddress = lookup("#nestedSegwitReceivingAddress").queryAs(TextField).text
            sendBitcoinAndWait(sixthAddress, 1.0, 6)
            sleep(TIMEOUT, SECONDS)
            String seventhAddress = lookup("#nestedSegwitReceivingAddress").queryAs(TextField).text
            sendBitcoinAndWait(seventhAddress, 1.0, 7)
            TableView addressesTableView = lookup("#addressesTable").queryAs(TableView)
            clickOn("Transactions")
            TableView transactionsTableView = lookup("#transactionsTable").queryAs(TableView)
            String labelText = lookup("#totalBalance").queryAs(Label).getText()
        then:
            addressesTableView.items.size() == 7
            transactionsTableView.items.size() == 7
            nestedSegwitAddressIsValid(firstAddress, mnemonicSeed, 0)
            nestedSegwitAddressIsValid(secondAddress, mnemonicSeed, 1)
            nestedSegwitAddressIsValid(thirdAddress, mnemonicSeed, 2)
            nestedSegwitAddressIsValid(fourthAddress, mnemonicSeed, 3)
            nestedSegwitAddressIsValid(fifthAddress, mnemonicSeed, 4)
            nestedSegwitAddressIsValid(sixthAddress, mnemonicSeed, 5)
            nestedSegwitAddressIsValid(seventhAddress, mnemonicSeed, 6)
            labelText == "Total Balance: 7.00000000 BTC (confirmed: 0.00000000, unconfirmed: 7.00000000)"
    }
}
