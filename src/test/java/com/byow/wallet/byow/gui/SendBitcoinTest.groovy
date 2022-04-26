package com.byow.wallet.byow.gui

import com.byow.wallet.byow.api.services.NodeEstimateFeeService
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

            IntStream.range(0, previousUtxosNumber).forEach {
                String address = lookup("#receivingAddress").queryAs(TextField).text
                BigDecimal amount = 1.0
                sendBitcoinAndWait(address, 1.0, 1, "#addressesTable", amount)
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
            clickOn("#transactionsTab")
            TableView<TransactionRow> transactionsTable = lookup("#transactionsTable").queryAs(TableView)
        then:
            amountToSendLabel == BitcoinFormatter.format(new BigDecimal(amountToSend))
            totalFeeLabel == totalFee
            totalLabel == totalSpent
            feeRateLabel == feeRate
            addressToSendLabel == nodeAddress
            transactionsTable.items.size() == previousUtxosNumber + 1
        where:
            previousUtxosNumber | amountToSend | totalFee         | totalSpent    | feeRate
            1                   | "0.5"        | "0.00002679"     | "0.50002679"  | "0.0002 BTC/kvByte"
            2                   | "1.5"        | "0.00003971"     | "1.50003971"  | "0.0002 BTC/kvByte"
    }

    private waitForDialog() {
        waitFor(TIMEOUT, SECONDS, {
            DialogPane dialogPane = lookup("#dialogPane").queryAs(DialogPane)
            return dialogPane != null
        })
    }
}
