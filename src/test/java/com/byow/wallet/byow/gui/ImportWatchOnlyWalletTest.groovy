package com.byow.wallet.byow.gui

import com.byow.wallet.byow.api.services.NodeEstimateFeeService
import com.byow.wallet.byow.domains.ExtendedPubkey
import com.byow.wallet.byow.observables.AddressRow
import com.byow.wallet.byow.observables.TransactionRow
import com.byow.wallet.byow.utils.ExtendedPubkeysSerializer
import javafx.scene.control.Label
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import org.springframework.boot.test.mock.mockito.MockBean

import static org.mockito.Mockito.when

class ImportWatchOnlyWalletTest extends GuiTest {
    @MockBean
    NodeEstimateFeeService nodeEstimateFeeService

    def setup() {
        loadWalletAndAddBalance()
        when(nodeEstimateFeeService.estimate()).thenReturn(0.0002)
    }

    def "should import watch only wallet and receive bitcoin"() {
        when:
            def walletName = "My Test Wallet 35"
            def mnemonicSeed = mnemonicSeedService.create()
            List<ExtendedPubkey> extendedPubkeys = createExtendedPubkeysService.create(mnemonicSeed, "")
            def extendedPubKeysJson = ExtendedPubkeysSerializer.serialize(extendedPubkeys)
            importWatchOnlyWallet(walletName, extendedPubKeysJson)
            String address = lookup("#receivingAddress").queryAs(TextField).text
            sendBitcoinAndWait(address, 0.00001, 1, "#addressesTable", 0.00001)

            clickOn("#addressesTab")
            TableView<AddressRow> addressesTable = lookup("#addressesTable").queryAs(TableView)

            clickOn("Transactions")
            TableView<TransactionRow> transactionsTable = lookup("#transactionsTable").queryAs(TableView)

            String labelText = lookup("#totalBalance").queryAs(Label).text

            def addressesTableSize = addressesTable.items.size()
            def transactionTableSize = transactionsTable.items.size()
        then:
            addressesTableSize == 1
            transactionTableSize == 1
            addressIsValid(address, mnemonicSeed, 0)
            labelText == "Total Balance: 0.00001000 BTC (confirmed: 0.00000000, unconfirmed: 0.00001000)"
    }

}
