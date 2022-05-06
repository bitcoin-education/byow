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

            TableView<AddressRow> addressesTable = lookup("#addressesTable").queryAs(TableView)
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

    void waitForDialog() {
        waitFor(TIMEOUT, SECONDS, {
            DialogPane dialogPane = lookup("#dialogPane").queryAs(DialogPane)
            return dialogPane != null
        })
    }
}
