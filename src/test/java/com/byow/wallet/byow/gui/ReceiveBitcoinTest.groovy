package com.byow.wallet.byow.gui

import com.byow.wallet.byow.observables.AddressRow
import com.byow.wallet.byow.observables.TransactionRow
import javafx.scene.control.Label
import javafx.scene.control.TableView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField

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
            waitLoadWallet()
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
            waitLoadWallet()
            String address = lookup("#receivingAddress").queryAs(TextField).text
            sendBitcoinAndWait(address)
            sendBitcoinAndWait(address, 2.0)
            TableView addressesTableView = lookup("#addressesTable").queryAs(TableView)
            clickOn("Transactions")
            TableView transactionsTableView = lookup("#transactionsTable").queryAs(TableView)
            String labelText = lookup("#totalBalance").queryAs(Label).getText()
        then:
            addressesTableView.items.size() == 1
            transactionsTableView.items.size() == 2
            addressIsValid(address, mnemonicSeed, 0)
            labelText == "Total Balance: 2.00000000 BTC (confirmed: 0.00000000, unconfirmed: 2.00000000)"
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
            waitLoadWallet()
            String address = lookup("#receivingAddress").queryAs(TextField).text
            sendBitcoinAndWait(address)
            String nextAddress = lookup("#receivingAddress").queryAs(TextField).text
            sendBitcoinAndWait(nextAddress, 1.0, 2)
            TableView addressesTableView = lookup("#addressesTable").queryAs(TableView)
            clickOn("Transactions")
            TableView transactionsTableView = lookup("#transactionsTable").queryAs(TableView)
            String labelText = lookup("#totalBalance").queryAs(Label).getText()
        then:
            addressesTableView.items.size() == 2
            transactionsTableView.items.size() == 2
            addressIsValid(address, mnemonicSeed, 0)
            addressIsValid(nextAddress, mnemonicSeed, 1)
            labelText == "Total Balance: 2.00000000 BTC (confirmed: 0.00000000, unconfirmed: 2.00000000)"
    }

    def "should receive bitcoins in seven transactions to different addresses"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            def walletName = "My Test Wallet 4"
            write(walletName)
            clickOn("Create")
            def mnemonicSeed = lookup("#mnemonicSeed").queryAs(TextArea).text
            clickOn("OK")
            clickOn("Receive")
            waitLoadWallet()
            String firstAddress = lookup("#receivingAddress").queryAs(TextField).text
            sendBitcoinAndWait(firstAddress)
            String secondAddress = lookup("#receivingAddress").queryAs(TextField).text
            sendBitcoinAndWait(secondAddress, 1.0, 2)
            String thirdAddress = lookup("#receivingAddress").queryAs(TextField).text
            sendBitcoinAndWait(thirdAddress, 1.0, 3)
            String fourthAddress = lookup("#receivingAddress").queryAs(TextField).text
            sendBitcoinAndWait(fourthAddress, 1.0, 4)
            String fifthAddress = lookup("#receivingAddress").queryAs(TextField).text
            sendBitcoinAndWait(fifthAddress, 1.0, 5)
            String sixthAddress = lookup("#receivingAddress").queryAs(TextField).text
            sendBitcoinAndWait(sixthAddress, 1.0, 6)
            String seventhAddress = lookup("#receivingAddress").queryAs(TextField).text
            sendBitcoinAndWait(seventhAddress, 1.0, 7)
            String lastReceivingAddress = lookup("#receivingAddress").queryAs(TextField).text
            TableView addressesTableView = lookup("#addressesTable").queryAs(TableView)
            clickOn("Transactions")
            TableView transactionsTableView = lookup("#transactionsTable").queryAs(TableView)
            String labelText = lookup("#totalBalance").queryAs(Label).getText()
            def addressesTableSize = addressesTableView.items.size()
            def transactionTableSize = transactionsTableView.items.size()

            loadWallet(walletName)
            TableView<TransactionRow> transactionsTableAfterLoad = lookup("#transactionsTable").queryAs(TableView)
            clickOn("#addressesTab")
            TableView<AddressRow> addressesTableAfterLoad = lookup("#addressesTable").queryAs(TableView)
            clickOn("Receive")
            String lastReceivingAddressAfterLoad = lookup("#receivingAddress").queryAs(TextField).text
            String labelTextAfterLoad = lookup("#totalBalance").queryAs(Label).text
        then:
            lastReceivingAddress == lastReceivingAddressAfterLoad
            addressesTableView.items.size() == 7
            transactionsTableView.items.size() == 7
            addressIsValid(firstAddress, mnemonicSeed, 0)
            addressIsValid(secondAddress, mnemonicSeed, 1)
            addressIsValid(thirdAddress, mnemonicSeed, 2)
            addressIsValid(fourthAddress, mnemonicSeed, 3)
            addressIsValid(fifthAddress, mnemonicSeed, 4)
            addressIsValid(sixthAddress, mnemonicSeed, 5)
            addressIsValid(seventhAddress, mnemonicSeed, 6)
            transactionsTableAfterLoad.items.size() == transactionTableSize
            addressesTableAfterLoad.items.size() == addressesTableSize
            labelText == "Total Balance: 7.00000000 BTC (confirmed: 0.00000000, unconfirmed: 7.00000000)"
            labelText == labelTextAfterLoad
    }

}
