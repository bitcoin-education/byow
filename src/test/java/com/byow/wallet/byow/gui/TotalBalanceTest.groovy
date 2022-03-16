package com.byow.wallet.byow.gui

import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.control.TextField

import static java.util.concurrent.TimeUnit.SECONDS

class TotalBalanceTest extends GuiTest {
    def setup() {
        loadWalletAndAddBalance()
    }

    def "should calculate total balance"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write("My Test Wallet 8")
            clickOn("Create")
            def mnemonicSeed = lookup("#mnemonicSeed").queryAs(TextArea).text
            clickOn("OK")
            clickOn("Receive")
            String address = lookup("#receivingAddress").queryAs(TextField).text
            sleep(TIMEOUT, SECONDS)
            sendBitcoinAndWait(address, 1.0, 1, "#transactionsTable")
            String labelText = lookup("#totalBalance").queryAs(Label).getText()
        then:
            labelText == "Total Balance: 1.00000000 BTC (confirmed: 0.00000000, unconfirmed: 1.00000000)"
    }
}
