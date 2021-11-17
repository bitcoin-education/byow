package com.byow.wallet.byow.gui

import javafx.scene.control.TextArea
import javafx.scene.control.TextField

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
}
