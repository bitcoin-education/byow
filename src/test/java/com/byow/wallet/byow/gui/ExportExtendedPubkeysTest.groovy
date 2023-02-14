package com.byow.wallet.byow.gui

import com.byow.wallet.byow.domains.ExtendedPubkey
import com.byow.wallet.byow.utils.ExtendedPubkeysSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import javafx.scene.control.TextArea

class ExportExtendedPubkeysTest extends GuiTest {
    ObjectMapper objectMapper = new ObjectMapper()

    def setup() {
        objectMapper = new ObjectMapper()
    }

    def "should export extended pubkeys from watch only wallet"() {
        when:
            def walletName = "My Test Wallet 37"
            def mnemonicSeed = mnemonicSeedService.create()
            List<ExtendedPubkey> extendedPubkeys = createExtendedPubkeysService.create(mnemonicSeed, "")
            def extendedPubKeysJson = ExtendedPubkeysSerializer.serialize(extendedPubkeys)
            importWatchOnlyWallet(walletName, extendedPubKeysJson)
            exportExtendedPubkeys()
            def extendedPubkeysExportedJson = lookup("#extendedPubkeys").queryAs(TextArea).text
            clickOn("OK")
            def extendedPubkeysExportedMap = objectMapper.readValue(extendedPubkeysExportedJson, Map.class)
        then:
            extendedPubkeys.collect { extendedPubkeysExportedMap.get(it.type) == it.key }.every()
    }

    def "should export extended pubkeys from wallet"() {
        when:
            def walletName = "My Test Wallet 38"
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write(walletName)
            clickOn("Create")
            def mnemonicSeed = lookup("#mnemonicSeed").queryAs(TextArea.class).text
            clickOn("OK")

            List<ExtendedPubkey> extendedPubkeys = createExtendedPubkeysService.create(mnemonicSeed, "")
            exportExtendedPubkeys()
            def extendedPubkeysExportedJson = lookup("#extendedPubkeys").queryAs(TextArea).text
            clickOn("OK")

            def extendedPubkeysExportedMap = objectMapper.readValue(extendedPubkeysExportedJson, Map.class)
        then:
            extendedPubkeys.collect { extendedPubkeysExportedMap.get(it.type) == it.key }.every()
    }

    def "should export extended pubkeys from wallet with password"() {
        when:
            def password = "asdf"
            def walletName = "My Test Wallet 39"

            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write(walletName)
            clickOn("#password")
            write(password)
            clickOn("Create")
            def mnemonicSeed = lookup("#mnemonicSeed").queryAs(TextArea.class).text
            clickOn("OK")

            List<ExtendedPubkey> extendedPubkeys = createExtendedPubkeysService.create(mnemonicSeed, password)
            exportExtendedPubkeys()
            def extendedPubkeysExportedJson = lookup("#extendedPubkeys").queryAs(TextArea).text
            clickOn("OK")

            def extendedPubkeysExportedMap = objectMapper.readValue(extendedPubkeysExportedJson, Map.class)
        then:
            extendedPubkeys.collect { extendedPubkeysExportedMap.get(it.type) == it.key }.every()

    }
}
