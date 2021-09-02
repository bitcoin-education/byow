package com.byow.wallet.byow.gui

import com.byow.wallet.byow.TestBase
import javafx.scene.control.TextArea

class CreateWalletTest extends TestBase {
    def "should create wallet"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write("My Test Wallet")
            clickOn("Create")
            def mnemonicSeed = lookup("#mnemonicSeed").queryAs(TextArea.class).text
        then:
            mnemonicSeed
    }
}
