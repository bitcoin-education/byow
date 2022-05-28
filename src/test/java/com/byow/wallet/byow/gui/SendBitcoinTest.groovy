package com.byow.wallet.byow.gui

import com.byow.wallet.byow.api.services.NodeEstimateFeeService
import com.byow.wallet.byow.observables.AddressRow
import com.byow.wallet.byow.observables.TransactionRow
import com.byow.wallet.byow.utils.BitcoinFormatter
import javafx.scene.control.DialogPane
import javafx.scene.control.Label
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import org.springframework.boot.test.mock.mockito.MockBean
import org.testfx.service.query.NodeQuery

import java.util.stream.IntStream

import static java.util.concurrent.TimeUnit.SECONDS
import static org.mockito.Mockito.when
import static org.testfx.util.WaitForAsyncUtils.waitFor

class SendBitcoinTest extends GuiTest {
    @MockBean
    NodeEstimateFeeService nodeEstimateFeeService

    def setup() {
        loadWalletAndAddBalance()
        when(nodeEstimateFeeService.estimate()).thenReturn(0.0002)
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
            IntStream.range(0, previousUtxosNumber).forEach{
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
            String totalFeeLabel = lookup("#totalFees").queryAs(Label).text
            String totalLabel = lookup("#total").queryAs(Label).text
            String feeRateLabel = lookup("#feeRate").queryAs(Label).text
            String addressToSendLabel = lookup("#addressToSendDialog").queryAs(Label).text

            clickOn("OK")
            sleep(TIMEOUT, SECONDS)

            TableView<TransactionRow> addressesTable = lookup("#addressesTable").queryAs(TableView)

            clickOn("#transactionsTab")
            TableView<TransactionRow> transactionsTable = lookup("#transactionsTable").queryAs(TableView)
            String totalBalanceText = lookup("#totalBalance").queryAs(Label).getText()
        then:
            addressesTable.items.size() == 1
            addressesTable.items[0].balance == changeAmount
            amountToSendLabel == BitcoinFormatter.format(new BigDecimal(amountToSend))
            totalFeeLabel == totalFee
            totalLabel == totalSpent
            feeRateLabel == feeRate
            addressToSendLabel == nodeAddress
            transactionsTable.items.size() == previousUtxosNumber + 1
            transactionsTable.items[0].balance == "-".concat(totalSpent)
            totalBalanceText == "Total Balance: $changeAmount BTC (confirmed: ${BitcoinFormatter.format(funds)}, unconfirmed: ${"-".concat(totalSpent)})"
        where:
            previousUtxosNumber | amountToSend | totalFee       | totalSpent    | changeAmount | feeRate
            1                   | "0.5"        | "0.00002679"   | "0.50002679"  | "0.49997321" | "0.0002 BTC/kvByte"
            2                   | "1.5"        | "0.00003971"   | "1.50003971"  | "0.49996029" | "0.0002 BTC/kvByte"
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
            String totalFeeLabel = lookup("#totalFees").queryAs(Label).text
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
            String totalFeeLabel = lookup("#totalFees").queryAs(Label).text
            String totalLabel = lookup("#total").queryAs(Label).text
            String feeRateLabel = lookup("#feeRate").queryAs(Label).text
            String addressToSendLabel = lookup("#addressToSendDialog").queryAs(Label).text
            clickOn("#walletPassword")
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

    private void sendBitcoin(String nodeAddress, String amountToSend) {
        clickOn("#amountToSend")
        write(amountToSend)
        clickOn("#addressToSend")
        write(nodeAddress)
        clickOn("#send")
        waitForDialog()
    }

    void waitForDialog() {
        waitFor(TIMEOUT, SECONDS, {
            DialogPane dialogPane = lookup("#dialogPane").queryAs(DialogPane)
            return dialogPane != null
        })
    }
}
