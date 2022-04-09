package com.byow.wallet.byow.gui

import com.byow.wallet.byow.observables.AddressRow
import com.byow.wallet.byow.observables.TransactionRow
import com.byow.wallet.byow.utils.BitcoinFormatter
import com.byow.wallet.byow.utils.Satoshi
import javafx.scene.control.DialogPane
import javafx.scene.control.Label
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import org.testfx.service.query.NodeQuery

import java.util.stream.IntStream

import static java.util.concurrent.TimeUnit.SECONDS
import static org.testfx.util.WaitForAsyncUtils.waitFor

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
            sleep(TIMEOUT, SECONDS)
            BigDecimal funds = 0
            IntStream.range(0, previousUtxosNumber).forEach {
                String address = lookup("#receivingAddress").queryAs(TextField).text
                BigDecimal amount = 1.0
                sendBitcoinAndWait(address, 1.0, 1, "#addressesTable", amount)
                funds += amount
            }
            String nodeAddress = nodeGetNewAddressClient.getNewAddress(TESTWALLET)
            nodeGenerateToAddressClient.generateToAddress(TESTWALLET, 1, nodeAddress)
            clickOn("#sendTab")
            clickOn("#amountToSend")
            write(amountToSend)
            clickOn("#addressToSend")
            write(nodeAddress)
            clickOn("#send")
            waitForDialog()
            String amountToSendLabel = lookup("#amountToSendDialog").queryAs(Label).text
            String totalFeeLabel = lookup("#totalFee").queryAs(Label).text
            String totalLabel = lookup("#total").queryAs(Label).text
            String feeRateLabel = lookup("#feeRate").queryAs(Label).text
            String addressToSendLabel = lookup("#addressToSendDialog").queryAs(Label).text
            clickOn("OK")
            sleep(TIMEOUT, SECONDS)
            TableView<AddressRow> addressesTable = lookup("#addressesTable").queryAs(TableView)
            clickOn("#transactionsTab")
            TableView<TransactionRow> transactionsTable = lookup("#transactionsTable").queryAs(TableView)
            String labelText = lookup("#totalBalance").queryAs(Label).getText()
        then:
            addressesTable.items.size() == 1
            addressesTable.items[0].balance == changeAmount
            transactionsTable.items.size() == previousUtxosNumber + 1
            transactionsTable.items[0].balance == "-".concat(totalSpent)
            amountToSendLabel == BitcoinFormatter.format(new BigDecimal(amountToSend))
            totalFeeLabel == totalFee
            totalLabel == totalSpent
            feeRateLabel == feeRate
            addressToSendLabel == nodeAddress
            labelText == "Total Balance: $changeAmount BTC (confirmed: ${BitcoinFormatter.format(funds)}, unconfirmed: ${"-".concat(totalSpent)})"
        where:
            previousUtxosNumber | amountToSend | totalFee         | totalSpent    | changeAmount | feeRate
            1                   | "0.5"        | "0.00002679"     | "0.50002679"  | "0.49997321" | "0.0002 BTC/kvByte"
            2                   | "1.5"        | "0.00003971"     | "1.50003971"  | "0.49996029" | "0.0002 BTC/kvByte"
    }

    def "should send bitcoin to address from self"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write("My Test Wallet 10")
            clickOn("Create")
            clickOn("OK")
            clickOn("Receive")
            sleep(TIMEOUT, SECONDS)
            BigDecimal funds = 0
            String lastUsedAddress = ""
            IntStream.range(0, previousUtxosNumber).forEach {
                String address = lookup("#receivingAddress").queryAs(TextField).text
                BigDecimal amount = 1.0
                sendBitcoinAndWait(address, 1.0, 1, "#addressesTable", amount)
                funds += amount
                lastUsedAddress = address
            }
            String nodeAddress = nodeGetNewAddressClient.getNewAddress(TESTWALLET)
            nodeGenerateToAddressClient.generateToAddress(TESTWALLET, 1, nodeAddress)
            clickOn("#sendTab")
            clickOn("#amountToSend")
            write(amountToSend)
            clickOn("#addressToSend")
            write(lastUsedAddress)
            clickOn("#send")
            waitForDialog()
            String amountToSendLabel = lookup("#amountToSendDialog").queryAs(Label).text
            String totalFeeLabel = lookup("#totalFee").queryAs(Label).text
            String totalLabel = lookup("#total").queryAs(Label).text
            String feeRateLabel = lookup("#feeRate").queryAs(Label).text
            String addressToSendLabel = lookup("#addressToSendDialog").queryAs(Label).text
            clickOn("OK")
            sleep(TIMEOUT, SECONDS)
            TableView<AddressRow> addressesTable = lookup("#addressesTable").queryAs(TableView)
            clickOn("#transactionsTab")
            TableView<TransactionRow> transactionsTable = lookup("#transactionsTable").queryAs(TableView)
            String labelText = lookup("#totalBalance").queryAs(Label).getText()
        then:
            addressesTable.items.size() == 2
            addressesTable.items.any {
                it.balance == changeAmount
            }
            addressesTable.items.any {
                it.balance == BitcoinFormatter.format(new BigDecimal(amountToSend))
            }
            transactionsTable.items.size() == previousUtxosNumber + 1
            transactionsTable.items[0].balance == "-".concat(totalSpent)
            amountToSendLabel == BitcoinFormatter.format(new BigDecimal(amountToSend))
            totalFeeLabel == totalFee
            totalLabel == totalSpent
            feeRateLabel == feeRate
            addressToSendLabel == lastUsedAddress
            labelText == "Total Balance: $totalBalance BTC (confirmed: ${BitcoinFormatter.format(funds)}, unconfirmed: ${"-".concat(totalSpent)})"
        where:
            previousUtxosNumber | amountToSend | totalFee         | totalSpent    | changeAmount | totalBalance | feeRate
            1                   | "0.5"        | "0.00002679"     | "0.00002679"  | "0.49997321" | "0.99997321" | "0.0002 BTC/kvByte"
            2                   | "1.5"        | "0.00003971"     | "0.00003971"  | "0.49996029" | "1.99996029" | "0.0002 BTC/kvByte"
    }

    def "should send bitcoin and confirm"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write("My Test Wallet 11")
            clickOn("Create")
            clickOn("OK")
            clickOn("Receive")
            sleep(TIMEOUT, SECONDS)
            BigDecimal funds = 0
            IntStream.range(0, previousUtxosNumber).forEach {
                String address = lookup("#receivingAddress").queryAs(TextField).text
                BigDecimal amount = 1.0
                sendBitcoinAndWait(address, 1.0, 1, "#addressesTable", amount)
                funds += amount
            }
            String nodeAddress = nodeGetNewAddressClient.getNewAddress(TESTWALLET)
            nodeGenerateToAddressClient.generateToAddress(TESTWALLET, 1, nodeAddress)
            clickOn("#sendTab")
            clickOn("#amountToSend")
            write(amountToSend)
            clickOn("#addressToSend")
            write(nodeAddress)
            clickOn("#send")
            waitForDialog()
            String amountToSendLabel = lookup("#amountToSendDialog").queryAs(Label).text
            String totalFeeLabel = lookup("#totalFee").queryAs(Label).text
            String totalLabel = lookup("#total").queryAs(Label).text
            String feeRateLabel = lookup("#feeRate").queryAs(Label).text
            String addressToSendLabel = lookup("#addressToSendDialog").queryAs(Label).text
            clickOn("OK")
            sleep(TIMEOUT, SECONDS)
            nodeGenerateToAddressClient.generateToAddress(TESTWALLET, 1, nodeAddress)
            TableView<AddressRow> addressesTable = lookup("#addressesTable").queryAs(TableView)
            clickOn("#transactionsTab")
            TableView<TransactionRow> transactionsTable = lookup("#transactionsTable").queryAs(TableView)
            String labelText = lookup("#totalBalance").queryAs(Label).getText()
        then:
            addressesTable.items.size() == 1
            addressesTable.items[0].balance == changeAmount
            addressesTable.items[0].confirmations == 1
            transactionsTable.items.size() == previousUtxosNumber + 1
            transactionsTable.items[0].balance == "-".concat(totalSpent)
            transactionsTable.items[0].confirmations == 1
            amountToSendLabel == BitcoinFormatter.format(new BigDecimal(amountToSend))
            totalFeeLabel == totalFee
            totalLabel == totalSpent
            feeRateLabel == feeRate
            addressToSendLabel == nodeAddress
            labelText == "Total Balance: $changeAmount BTC (confirmed: $changeAmount, unconfirmed: 0.00000000)"
        where:
            previousUtxosNumber | amountToSend | totalFee         | totalSpent    | changeAmount | feeRate
            1                   | "0.5"        | "0.00002679"     | "0.50002679"  | "0.49997321" | "0.0002 BTC/kvByte"
            2                   | "1.5"        | "0.00003971"     | "1.50003971"  | "0.49996029" | "0.0002 BTC/kvByte"
    }

    def "should send bitcoin 2 times"() {
        when:
        clickOn("New")
        clickOn("Wallet")
        clickOn("#name")
        write("My Test Wallet 12")
        clickOn("Create")
            clickOn("OK")
            clickOn("Receive")
            sleep(TIMEOUT, SECONDS)
            BigDecimal funds = 0
            IntStream.range(0, previousUtxosNumber).forEach {
                String address = lookup("#receivingAddress").queryAs(TextField).text
                BigDecimal amount = 1.0
                sendBitcoinAndWait(address, 1.0, 1, "#addressesTable", amount)
                funds += amount
            }
            String nodeAddress = nodeGetNewAddressClient.getNewAddress(TESTWALLET)
            nodeGenerateToAddressClient.generateToAddress(TESTWALLET, 1, nodeAddress)
            clickOn("#sendTab")
            sendBitcoin(nodeAddress, amountToSend)
            clickOn("OK")
            nodeGenerateToAddressClient.generateToAddress(TESTWALLET, 1, nodeAddress)
            sleep(TIMEOUT, SECONDS)
            TableView<AddressRow> addressesTable = lookup("#addressesTable").queryAs(TableView)
            String firstChangeAddress = addressesTable.items.first().address
            sendBitcoin(nodeAddress, amountToSend)
            String amountToSendLabel = lookup("#amountToSendDialog").queryAs(Label).text
            String totalFeeLabel = lookup("#totalFee").queryAs(Label).text
            String totalLabel = lookup("#total").queryAs(Label).text
            String feeRateLabel = lookup("#feeRate").queryAs(Label).text
            String addressToSendLabel = lookup("#addressToSendDialog").queryAs(Label).text
            clickOn("OK")
            sleep(TIMEOUT, SECONDS)
            addressesTable = lookup("#addressesTable").queryAs(TableView)
            String secondChangeAddress = addressesTable.items.first().address
            clickOn("#transactionsTab")
            TableView<TransactionRow> transactionsTable = lookup("#transactionsTable").queryAs(TableView)
            String labelText = lookup("#totalBalance").queryAs(Label).getText()
        then:
            firstChangeAddress != secondChangeAddress
            addressesTable.items.size() == 1
            addressesTable.items[0].balance == changeAmount
            transactionsTable.items.size() == previousUtxosNumber + 2
            transactionsTable.items[0].balance == "-".concat(totalSpent)
            amountToSendLabel == BitcoinFormatter.format(new BigDecimal(amountToSend))
            totalFeeLabel == totalFee
            totalLabel == totalSpent
            feeRateLabel == feeRate
            addressToSendLabel == nodeAddress
            labelText == "Total Balance: $changeAmount BTC (confirmed: ${BitcoinFormatter.format(funds - new BigDecimal(totalSpent)) }, unconfirmed: ${"-".concat(totalSpent)})"
        where:
            previousUtxosNumber | amountToSend  | totalFee         | totalSpent    | changeAmount | feeRate
            1                   | "0.25"        | "0.00002679"     | "0.25002679"  | "0.49994642" | "0.0002 BTC/kvByte"
    }

    def "should send bitcoin without change and confirm"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write("My Test Wallet 13")
            clickOn("Create")
            clickOn("OK")
            clickOn("Receive")
            sleep(TIMEOUT, SECONDS)
            BigDecimal funds = 0
            IntStream.range(0, previousUtxosNumber).forEach {
                String address = lookup("#receivingAddress").queryAs(TextField).text
                sendBitcoinAndWait(address, fundAmount, 1, "#addressesTable", fundAmount)
                funds += fundAmount
            }
            String nodeAddress = nodeGetNewAddressClient.getNewAddress(TESTWALLET)
            nodeGenerateToAddressClient.generateToAddress(TESTWALLET, 1, nodeAddress)
            clickOn("#sendTab")
            clickOn("#amountToSend")
            write(amountToSend)
            clickOn("#addressToSend")
            write(nodeAddress)
            clickOn("#send")
            waitForDialog()
            String amountToSendLabel = lookup("#amountToSendDialog").queryAs(Label).text
            String totalFeeLabel = lookup("#totalFee").queryAs(Label).text
            String totalLabel = lookup("#total").queryAs(Label).text
            String feeRateLabel = lookup("#feeRate").queryAs(Label).text
            String addressToSendLabel = lookup("#addressToSendDialog").queryAs(Label).text
            clickOn("OK")
            sleep(TIMEOUT, SECONDS)
            nodeGenerateToAddressClient.generateToAddress(TESTWALLET, 1, nodeAddress)
            TableView<AddressRow> addressesTable = lookup("#addressesTable").queryAs(TableView)
            clickOn("#transactionsTab")
            TableView<TransactionRow> transactionsTable = lookup("#transactionsTable").queryAs(TableView)
            String labelText = lookup("#totalBalance").queryAs(Label).getText()
        then:
            addressesTable.items.size() == 0
            transactionsTable.items.size() == previousUtxosNumber + 1
            transactionsTable.items[0].balance == "-".concat(totalSpent)
            transactionsTable.items[0].confirmations == 1
            amountToSendLabel == BitcoinFormatter.format(new BigDecimal(amountToSend))
            totalFeeLabel == totalFee
            totalLabel == totalSpent
            feeRateLabel == feeRate
            addressToSendLabel == nodeAddress
            labelText == "Total Balance: $changeAmount BTC (confirmed: $changeAmount, unconfirmed: 0.00000000)"
        where:
            previousUtxosNumber | amountToSend | totalFee         | totalSpent    | changeAmount | fundAmount | feeRate
            1                   | "1.00000000" | "0.00002090"     | "1.00002090"  | "0.00000000" | 1.00002090 | "0.0002 BTC/kvByte"
            1                   | "1.00000000" | "0.00002383"     | "1.00002383"  | "0.00000000" | 1.00002383 | "0.0002 BTC/kvByte"
            1                   | "1.00000000" | "0.00002384"     | "1.00002384"  | "0.00000000" | 1.00002384 | "0.0002 BTC/kvByte"
    }

    def "should send bitcoin with password"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write("My Test Wallet 14")
            clickOn("#password")
            String password = "mytestpassword"
            write(password)
            clickOn("Create")
            clickOn("OK")
            clickOn("Receive")
            sleep(TIMEOUT, SECONDS)
            BigDecimal funds = 0
            IntStream.range(0, previousUtxosNumber).forEach {
                String address = lookup("#receivingAddress").queryAs(TextField).text
                BigDecimal amount = 1.0
                sendBitcoinAndWait(address, 1.0, 1, "#addressesTable", amount)
                funds += amount
            }
            String nodeAddress = nodeGetNewAddressClient.getNewAddress(TESTWALLET)
            nodeGenerateToAddressClient.generateToAddress(TESTWALLET, 1, nodeAddress)
            clickOn("#sendTab")
            clickOn("#amountToSend")
            write(amountToSend)
            clickOn("#addressToSend")
            write(nodeAddress)
            clickOn("#send")
            waitForDialog()
            String amountToSendLabel = lookup("#amountToSendDialog").queryAs(Label).text
            String totalFeeLabel = lookup("#totalFee").queryAs(Label).text
            String totalLabel = lookup("#total").queryAs(Label).text
            String feeRateLabel = lookup("#feeRate").queryAs(Label).text
            String addressToSendLabel = lookup("#addressToSendDialog").queryAs(Label).text
            clickOn("#sendTransactionPassword")
            write(password)
            clickOn("OK")
            sleep(TIMEOUT, SECONDS)
            TableView<AddressRow> addressesTable = lookup("#addressesTable").queryAs(TableView)
            clickOn("#transactionsTab")
            TableView<TransactionRow> transactionsTable = lookup("#transactionsTable").queryAs(TableView)
            String labelText = lookup("#totalBalance").queryAs(Label).getText()
        then:
            addressesTable.items.size() == 1
            addressesTable.items[0].balance == changeAmount
            transactionsTable.items.size() == previousUtxosNumber + 1
            transactionsTable.items[0].balance == "-".concat(totalSpent)
            amountToSendLabel == BitcoinFormatter.format(new BigDecimal(amountToSend))
            totalFeeLabel == totalFee
            totalLabel == totalSpent
            feeRateLabel == feeRate
            addressToSendLabel == nodeAddress
            labelText == "Total Balance: $changeAmount BTC (confirmed: ${BitcoinFormatter.format(funds)}, unconfirmed: ${"-".concat(totalSpent)})"
        where:
            previousUtxosNumber | amountToSend | totalFee         | totalSpent    | changeAmount | feeRate
            1                   | "0.5"        | "0.00002679"     | "0.50002679"  | "0.49997321" | "0.0002 BTC/kvByte"
    }

    def "should not send bitcoin with wrong password"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write("My Test Wallet 15")
            clickOn("#password")
            String password = "mytestpassword"
            write(password)
            clickOn("Create")
            clickOn("OK")
            clickOn("Receive")
            sleep(TIMEOUT, SECONDS)
            BigDecimal funds = 0
            IntStream.range(0, previousUtxosNumber).forEach {
                String address = lookup("#receivingAddress").queryAs(TextField).text
                BigDecimal amount = 1.0
                sendBitcoinAndWait(address, 1.0, 1, "#addressesTable", amount)
                funds += amount
            }
            String formattedFunds = BitcoinFormatter.format(funds)
            String nodeAddress = nodeGetNewAddressClient.getNewAddress(TESTWALLET)
            nodeGenerateToAddressClient.generateToAddress(TESTWALLET, 1, nodeAddress)
            clickOn("#sendTab")
            clickOn("#amountToSend")
            write(amountToSend)
            clickOn("#addressToSend")
            write(nodeAddress)
            clickOn("#send")
            waitForDialog()
            String amountToSendLabel = lookup("#amountToSendDialog").queryAs(Label).text
            String totalFeeLabel = lookup("#totalFee").queryAs(Label).text
            String totalLabel = lookup("#total").queryAs(Label).text
            String feeRateLabel = lookup("#feeRate").queryAs(Label).text
            String addressToSendLabel = lookup("#addressToSendDialog").queryAs(Label).text
            clickOn("#sendTransactionPassword")
            write(password.concat("wrong"))
            clickOn("OK")
            String errorMessage = "Could not send transaction: wrong password."
            NodeQuery nodeQuery = lookup(errorMessage)
            clickOn("OK")
            TableView<AddressRow> addressesTable = lookup("#addressesTable").queryAs(TableView)
            clickOn("#transactionsTab")
            TableView<TransactionRow> transactionsTable = lookup("#transactionsTable").queryAs(TableView)
            String labelText = lookup("#totalBalance").queryAs(Label).getText()
        then:
            nodeQuery.queryLabeled().getText() == errorMessage
            addressesTable.items.size() == 1
            addressesTable.items[0].balance == formattedFunds
            transactionsTable.items.size() == 1
            transactionsTable.items[0].balance == formattedFunds
            amountToSendLabel == BitcoinFormatter.format(new BigDecimal(amountToSend))
            totalFeeLabel == totalFee
            totalLabel == totalSpent
            feeRateLabel == feeRate
            addressToSendLabel == nodeAddress
            labelText == "Total Balance: $formattedFunds BTC (confirmed: $formattedFunds, unconfirmed: 0.00000000)"
        where:
            previousUtxosNumber | amountToSend | totalFee         | totalSpent    | changeAmount | feeRate
            1                   | "0.5"        | "0.00002679"     | "0.50002679"  | "0.49997321" | "0.0002 BTC/kvByte"
    }

    def "should not send bitcoin without funds greater than amount + fee"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write("My Test Wallet 16")
            clickOn("Create")
            clickOn("OK")
            clickOn("Receive")
            sleep(TIMEOUT, SECONDS)
            BigDecimal funds = 0
            IntStream.range(0, previousUtxosNumber).forEach {
                String address = lookup("#receivingAddress").queryAs(TextField).text
                sendBitcoinAndWait(address, previousAmount, 1, "#addressesTable", previousAmount)
                funds += previousAmount
            }
            String nodeAddress = nodeGetNewAddressClient.getNewAddress(TESTWALLET)
            nodeGenerateToAddressClient.generateToAddress(TESTWALLET, 1, nodeAddress)
            clickOn("#sendTab")
            clickOn("#amountToSend")
            write(amountToSend)
            clickOn("#addressToSend")
            write(nodeAddress)
            clickOn("#send")
            String errorMessage = "Could not send transaction: not enough funds"
            NodeQuery nodeQuery = lookup(errorMessage)
            clickOn("OK")
        then:
            nodeQuery.queryLabeled().getText() == errorMessage
        where:
            previousUtxosNumber | amountToSend | previousAmount
            1                   | "0.5"        | 0.1
            1                   | "0.5"        | 0.50002089
    }

    def "should not send bitcoin without loaded wallet"() {
        when:
            String nodeAddress = nodeGetNewAddressClient.getNewAddress(TESTWALLET)
            clickOn("#sendTab")
            clickOn("#amountToSend")
            write(amountToSend)
            clickOn("#addressToSend")
            write(nodeAddress)
            clickOn("#send")
            String errorMessage = "Could not send transaction: wallet not loaded"
            NodeQuery nodeQuery = lookup(errorMessage)
            clickOn("OK")
        then:
            nodeQuery.queryLabeled().getText() == errorMessage
        where:
            previousUtxosNumber | amountToSend | previousAmount
            1                   | "0.5"        | 0.1
    }

    def "should not send bitcoin without any funds"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write("My Test Wallet 17")
            clickOn("Create")
            clickOn("OK")
            String nodeAddress = nodeGetNewAddressClient.getNewAddress(TESTWALLET)
            clickOn("#sendTab")
            clickOn("#amountToSend")
            write(amountToSend)
            clickOn("#addressToSend")
            write(nodeAddress)
            clickOn("#send")
            String errorMessage = "Could not send transaction: not enough funds"
            NodeQuery nodeQuery = lookup(errorMessage)
            clickOn("OK")
        then:
            nodeQuery.queryLabeled().getText() == errorMessage
        where:
            previousUtxosNumber | amountToSend | previousAmount
            1                   | "0.5"        | 0.1
    }

    def "should not send dust bitcoin"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write("My Test Wallet 18")
            clickOn("Create")
            clickOn("OK")
            clickOn("Receive")
            sleep(TIMEOUT, SECONDS)
            BigDecimal funds = 0
            IntStream.range(0, previousUtxosNumber).forEach {
                String address = lookup("#receivingAddress").queryAs(TextField).text
                sendBitcoinAndWait(address, previousAmount, 1, "#addressesTable", previousAmount)
                funds += previousAmount
            }
            String nodeAddress = nodeGetNewAddressClient.getNewAddress(TESTWALLET)
            nodeGenerateToAddressClient.generateToAddress(TESTWALLET, 1, nodeAddress)
            clickOn("#sendTab")
            clickOn("#amountToSend")
            write(amountToSend)
            clickOn("#addressToSend")
            write(nodeAddress)
            clickOn("#send")
            waitForDialog()
            clickOn("OK")
            String errorMessage = "Could not send transaction: amount to send is dust"
            NodeQuery nodeQuery = lookup(errorMessage)
            clickOn("OK")
        then:
            nodeQuery.queryLabeled().getText() == errorMessage
        where:
            previousUtxosNumber | amountToSend | previousAmount
            1                   | "0.00000293" | 0.1
    }

    def "should send bitcoin additional test"() {
        when:
        clickOn("New")
        clickOn("Wallet")
        clickOn("#name")
        write("My Test Wallet 19")
        clickOn("Create")
            clickOn("OK")
            clickOn("Receive")
            sleep(TIMEOUT, SECONDS)
            BigDecimal funds = 0
            IntStream.range(0, previousUtxosNumber).forEach {
                String address = lookup("#receivingAddress").queryAs(TextField).text
                BigDecimal amount = Satoshi.toBtc(utxoAmounts[it])
                sendBitcoinAndWait(address, amount, it+1, "#addressesTable", amount)
                funds += amount
            }
            String nodeAddress = nodeGetNewAddressClient.getNewAddress(TESTWALLET)
            nodeGenerateToAddressClient.generateToAddress(TESTWALLET, 1, nodeAddress)
            clickOn("#sendTab")
            clickOn("#amountToSend")
            String amountToSend = "1"
            write(amountToSend)
            clickOn("#addressToSend")
            write(nodeAddress)
            clickOn("#send")
            waitForDialog()
            String amountToSendLabel = lookup("#amountToSendDialog").queryAs(Label).text
            String totalFeeLabel = lookup("#totalFee").queryAs(Label).text
            String totalLabel = lookup("#total").queryAs(Label).text
            String feeRateLabel = lookup("#feeRate").queryAs(Label).text
            String addressToSendLabel = lookup("#addressToSendDialog").queryAs(Label).text
            clickOn("OK")
            sleep(TIMEOUT, SECONDS)
            TableView<AddressRow> addressesTable = lookup("#addressesTable").queryAs(TableView)
            clickOn("#transactionsTab")
            TableView<TransactionRow> transactionsTable = lookup("#transactionsTable").queryAs(TableView)
            String labelText = lookup("#totalBalance").queryAs(Label).getText()
        then:
            if (changeIsDust) {
                addressesTable.items.size() == 0
            } else {
                addressesTable.items.size() == 1
                addressesTable.items[0].balance == changeAmount
            }
            transactionsTable.items.size() == previousUtxosNumber + 1
            transactionsTable.items[0].balance == "-".concat(totalSpent)
            amountToSendLabel == BitcoinFormatter.format(new BigDecimal(amountToSend))
            totalFeeLabel == totalFee
            totalLabel == totalSpent
            feeRateLabel == "0.0002 BTC/kvByte"
            addressToSendLabel == nodeAddress
            labelText == "Total Balance: $changeAmount BTC (confirmed: ${BitcoinFormatter.format(funds)}, unconfirmed: ${"-".concat(totalSpent)})"
        where:
            previousUtxosNumber | totalFee                 | utxoAmounts                          | totalSpent    | changeAmount | changeIsDust
            1                   | "0.00002090"             | [100_002_090]                        | "1.00002090"  | "0.00000000" | true
            1                   | "0.00002091"             | [100_002_091]                        | "1.00002091"  | "0.00000000" | true
            1                   | "0.00002383"             | [100_002_383]                        | "1.00002383"  | "0.00000000" | true
            1                   | "0.00002384"             | [100_002_384]                        | "1.00002384"  | "0.00000000" | true
            1                   | "0.00002678"             | [100_002_678]                        | "1.00002678"  | "0.00000000" | true
            1                   | "0.00002679"             | [100_002_679]                        | "1.00002679"  | "0.00000000" | true
            1                   | "0.00002972"             | [100_002_972]                        | "1.00002972"  | "0.00000000" | true
            1                   | "0.00002679"             | [100_002_973]                        | "1.00002679"  | "0.00000294" | false
            2                   | "0.00003382"             | [50_001_691, 50_001_691]             | "1.00003382"  | "0.00000000" | true
            2                   | "0.00003383"             | [50_001_691, 50_001_692]             | "1.00003383"  | "0.00000000" | true
            2                   | "0.00003675"             | [50_001_691, 50_001_984]             | "1.00003675"  | "0.00000000" | true
            2                   | "0.00003676"             | [50_001_691, 50_001_985]             | "1.00003676"  | "0.00000000" | true
            2                   | "0.00003970"             | [50_001_691, 50_002_279]             | "1.00003970"  | "0.00000000" | true
            2                   | "0.00003971"             | [50_001_691, 50_002_280]             | "1.00003971"  | "0.00000000" | true
            2                   | "0.00004264"             | [50_001_691, 50_002_573]             | "1.00004264"  | "0.00000000" | true
            2                   | "0.00003971"             | [50_001_691, 50_002_574]             | "1.00003971"  | "0.00000294" | false
            3                   | "0.00004674"             | [33_334_891, 33_334_891, 33_334_892] | "1.00004674"  | "0.00000000" | true
            3                   | "0.00004675"             | [33_334_891, 33_334_891, 33_334_893] | "1.00004675"  | "0.00000000" | true
            3                   | "0.00004967"             | [33_334_891, 33_334_891, 33_335_185] | "1.00004967"  | "0.00000000" | true
            3                   | "0.00004968"             | [33_334_891, 33_334_891, 33_335_186] | "1.00004968"  | "0.00000000" | true
            3                   | "0.00005262"             | [33_334_891, 33_334_891, 33_335_480] | "1.00005262"  | "0.00000000" | true
            3                   | "0.00005263"             | [33_334_891, 33_334_891, 33_335_481] | "1.00005263"  | "0.00000000" | true
            3                   | "0.00005556"             | [33_334_891, 33_334_891, 33_335_774] | "1.00005556"  | "0.00000000" | true
            3                   | "0.00005263"             | [33_334_891, 33_334_891, 33_335_775] | "1.00005263"  | "0.00000294" | false
    }

    private void sendBitcoin(String nodeAddress, String amountToSend) {
        clickOn("#amountToSend")
        write(amountToSend)
        clickOn("#addressToSend")
        write(nodeAddress)
        clickOn("#send")
        waitForDialog()
    }

    private waitForDialog() {
        waitFor(TIMEOUT, SECONDS, {
            DialogPane dialogPane = lookup("#dialogPane").queryAs(DialogPane)
            return dialogPane != null
        })
    }
}
