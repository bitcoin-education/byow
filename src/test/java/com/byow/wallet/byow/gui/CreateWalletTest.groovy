package com.byow.wallet.byow.gui

import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import org.testfx.service.query.NodeQuery

import java.util.concurrent.TimeUnit

class CreateWalletTest extends GuiTest {
    def "should create wallet"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write("My Test Wallet")
            clickOn("Create")
            def mnemonicSeed = lookup("#mnemonicSeed").queryAs(TextArea.class).text
            clickOn("OK")
            clickOn("Receive")
            String address = lookup("#receivingAddress").queryAs(TextField.class).getText()
        then:
            mnemonicSeed
            stage.title == "BYOW Wallet - " + "My Test Wallet"
            address
    }

    def "should cancel wallet creation"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("Cancel")
        then:
            noExceptionThrown()
    }

    def "should not create wallet with repeated name"() {
        given:
            def walletName = "My Test Wallet 32"
            def password = ""
            def mnemonicSeed = mnemonicSeedService.create()
            createWallet(walletName, password, mnemonicSeed)
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write(walletName)
            clickOn("Create")
            clickOn("OK")
            sleep(5, TimeUnit.SECONDS)

            String errorMessage = "Could not create wallet: the wallet name already exists."
            NodeQuery nodeQuery = lookup(errorMessage)
            clickOn("OK")
        then:
            nodeQuery.queryLabeled().text == errorMessage
    }
}
