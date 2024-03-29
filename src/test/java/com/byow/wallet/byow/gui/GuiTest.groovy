package com.byow.wallet.byow.gui

import com.byow.wallet.byow.api.services.CreateExtendedPubkeysService
import com.byow.wallet.byow.api.services.CreateWalletService
import com.byow.wallet.byow.api.services.CreateWatchOnlyWalletService
import com.byow.wallet.byow.api.services.ExtendedPubkeyService
import com.byow.wallet.byow.api.services.MnemonicSeedService
import com.byow.wallet.byow.api.services.NestedSegwitAddressGenerator
import com.byow.wallet.byow.api.services.SegwitAddressGenerator
import com.byow.wallet.byow.api.services.node.NodeLoadOrCreateWalletService
import com.byow.wallet.byow.api.services.node.client.NodeGenerateToAddressClient
import com.byow.wallet.byow.api.services.node.client.NodeGetBalanceClient
import com.byow.wallet.byow.api.services.node.client.NodeGetNewAddressClient
import com.byow.wallet.byow.api.services.node.client.NodeListWalletsClient
import com.byow.wallet.byow.api.services.node.client.NodeSendToAddressClient
import com.byow.wallet.byow.database.repositories.ExtendedPubkeyRepository
import com.byow.wallet.byow.database.repositories.WalletRepository
import com.byow.wallet.byow.database.repositories.WatchOnlyPasswordRepository
import com.byow.wallet.byow.database.services.SaveWalletService
import com.byow.wallet.byow.database.services.SaveWatchOnlyWalletService
import com.byow.wallet.byow.domains.AddressType
import com.byow.wallet.byow.domains.Wallet
import com.byow.wallet.byow.gui.events.GuiStartedEvent
import com.byow.wallet.byow.gui.services.WatchOnlyPasswordService
import com.byow.wallet.byow.observables.CurrentWallet
import com.byow.wallet.byow.utils.BitcoinFormatter
import com.byow.wallet.byow.utils.Copy
import io.github.bitcoineducation.bitcoinjava.AddressConstants
import io.github.bitcoineducation.bitcoinjava.ExtendedKeyPrefixes
import io.github.bitcoineducation.bitcoinjava.ExtendedPrivateKey
import io.github.bitcoineducation.bitcoinjava.ExtendedPubkey
import io.github.bitcoineducation.bitcoinjava.MnemonicSeed
import javafx.application.Platform
import javafx.scene.control.DialogPane
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.stage.Stage
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import org.testfx.framework.spock.ApplicationSpec

import java.security.Security

import static io.github.bitcoineducation.bitcoinjava.ExtendedKeyPrefixes.MAINNET_SEGWIT_PREFIX
import static java.util.concurrent.TimeUnit.MILLISECONDS
import static java.util.concurrent.TimeUnit.SECONDS
import static org.testfx.util.WaitForAsyncUtils.waitFor

@SpringBootTest
@ActiveProfiles('test')
abstract class GuiTest extends ApplicationSpec {

    public static final String TESTWALLET = "testwallet"

    public static final int TIMEOUT = 10

    @Autowired
    protected CreateExtendedPubkeysService createExtendedPubkeysService

    @Autowired
    protected NodeSendToAddressClient nodeSendToAddressClient

    @Autowired
    protected NodeLoadOrCreateWalletService nodeLoadOrCreateWalletService

    @Autowired
    protected NodeGetBalanceClient nodeGetBalanceClient

    @Autowired
    protected NodeGetNewAddressClient nodeGetNewAddressClient

    @Autowired
    protected NodeGenerateToAddressClient nodeGenerateToAddressClient

    @Autowired
    protected ExtendedPubkeyService extendedPubkeyService

    @Autowired
    protected SegwitAddressGenerator segwitAddressGenerator

    @Autowired
    protected ApplicationContext context

    @Autowired
    protected NestedSegwitAddressGenerator nestedSegwitAddressGenerator

    @Autowired
    protected MnemonicSeedService mnemonicSeedService

    @Autowired
    protected CreateWalletService createWalletService

    @Autowired
    protected SaveWalletService saveWalletService

    @Autowired
    protected WalletRepository walletRepository

    @Autowired
    protected CurrentWallet currentWallet

    @Autowired
    protected NodeListWalletsClient nodeListWalletsClient

    @Autowired
    protected CreateWatchOnlyWalletService createWatchOnlyWalletService

    @Autowired
    protected ExtendedPubkeyRepository extendedPubkeyRepository

    @Autowired
    protected WatchOnlyPasswordRepository watchOnlyPasswordWalletRepository

    @Autowired
    protected SaveWatchOnlyWalletService saveWatchOnlyWalletService

    @Autowired
    protected WatchOnlyPasswordService watchOnlyPasswordService

    protected Stage stage

    @Override
    void init() throws Exception {
        FxToolkit.registerStage { new Stage() }
    }

    @Override
    void stop() throws Exception {
        FxToolkit.hideStage()
    }

    @Override
    void start(Stage stage) throws Exception {
        Security.addProvider(new BouncyCastleProvider())
        extendedPubkeyRepository.deleteAll()
        watchOnlyPasswordWalletRepository.deleteAll()
        walletRepository.deleteAll()
        this.stage = stage
        this.context.publishEvent(new GuiStartedEvent(this, stage));
    }

    def loadWalletAndAddBalance() {
        nodeLoadOrCreateWalletService.loadOrCreateWallet(TESTWALLET, false, false, "", true, false)
        createBalanceIfNecessary()
    }

    protected void sendBitcoinAndWait(String address, double expectedTotalAmount = 1.0, int expectedTotalSize = 1, String lookupComponent = "#addressesTable", double amount = 1.0, String receivingAddressComponent = "#receivingAddress") {
        nodeSendToAddressClient.sendToAddress(TESTWALLET, address, amount)
        waitFor(TIMEOUT, SECONDS, {
            TableView tableView = lookup(lookupComponent).queryAs(TableView)
            return tableView?.items?.size() == expectedTotalSize &&
                    tableView?.items ?[expectedTotalSize - 1]?.balance == BitcoinFormatter.format(expectedTotalAmount) &&
                    lookup(receivingAddressComponent).queryAs(TextField).text != address
        })
    }

    protected void createBalanceIfNecessary() {
        double balance = nodeGetBalanceClient.get(TESTWALLET)
        if (balance < 50) {
            String address = nodeGetNewAddressClient.getNewAddress(TESTWALLET, "bech32")
            nodeGenerateToAddressClient.generateToAddress(TESTWALLET, 150, address)
        }
    }

    protected boolean addressIsValid(String address, String mnemonicSeedString, Integer index, String password = "") {
        MnemonicSeed mnemonicSeed = new MnemonicSeed(mnemonicSeedString)
        ExtendedPrivateKey masterKey = mnemonicSeed.toMasterKey(password, ExtendedKeyPrefixes.MAINNET_PREFIX.getPrivatePrefix())
        String extendedPubkeyString = extendedPubkeyService.create(masterKey, "84'/0'/0'/0/".concat(index.toString()), AddressType.SEGWIT, MAINNET_SEGWIT_PREFIX).getKey()
        ExtendedPubkey extendedPubkey = ExtendedPubkey.unserialize(extendedPubkeyString)
        String expectedAddress = segwitAddressGenerator.generate(extendedPubkey, AddressConstants.REGTEST_P2WPKH_ADDRESS_PREFIX)
        return expectedAddress == address
    }

    protected boolean nestedSegwitAddressIsValid(String address, String mnemonicSeedString, Integer index) {
        MnemonicSeed mnemonicSeed = new MnemonicSeed(mnemonicSeedString)
        ExtendedPrivateKey masterKey = mnemonicSeed.toMasterKey("", ExtendedKeyPrefixes.MAINNET_PREFIX.getPrivatePrefix())
        String extendedPubkeyString = extendedPubkeyService.create(masterKey, "49'/0'/0'/0/".concat(index.toString()), AddressType.NESTED_SEGWIT, MAINNET_SEGWIT_PREFIX).getKey()
        ExtendedPubkey extendedPubkey = ExtendedPubkey.unserialize(extendedPubkeyString)
        String expectedAddress = nestedSegwitAddressGenerator.generate(extendedPubkey, AddressConstants.TESTNET_P2SH_ADDRESS_PREFIX)
        return expectedAddress == address
    }

    protected void waitForDialog() {
        waitFor(TIMEOUT, SECONDS, {
            DialogPane dialogPane = lookup("#dialogPane").queryAs(DialogPane)
            return dialogPane != null
        })
    }

    protected void sendBitcoin(String nodeAddress, String amountToSend, boolean waitForDialog = true) {
        clickOn("#amountToSend")
        write(amountToSend)
        clickOn("#addressToSend")
        write(nodeAddress)
        clickOn("#send")
        if (waitForDialog) {
            this.waitForDialog()
        }
    }

    protected Wallet createWallet(String walletName, String password, String mnemonicSeed) {
        Wallet wallet = createWalletService.create(walletName, password, mnemonicSeed, new Date(), 3)
        saveWalletService.saveWallet(wallet)
        return wallet
    }

    protected Wallet createWatchOnlyWallet(String walletName, String extendedPubkeysJson, String password = "") {
        Wallet wallet = createWatchOnlyWalletService.create(walletName, extendedPubkeysJson, new Date(), 3)
        saveWatchOnlyWalletService.saveWallet(wallet, watchOnlyPasswordService.encrypt(password))
        return wallet
    }

    protected void loadWallet(String walletName, String password = "") {
        clickOn("Load")
        moveTo("Wallet")
        clickOn(walletName)
        clickOn("#loadWalletPassword")
        write(password)
        clickOn("OK")
        sleep(TIMEOUT, SECONDS)
    }

    void waitLoadWallet() {
        waitFor(60, SECONDS, {
            sleep(1, SECONDS)
            return currentWallet.firstAddress && nodeListWalletsClient.listLoaded().contains(currentWallet.firstAddress)
        })
    }

    protected void importWallet(String name, String mnemonicSeed, String password = "") {
        clickOn("Import")
        clickOn("Wallet")
        clickOn("#walletName")
        write(name)
        clickOn("#walletPassword")
        write(password)
        clickOn("#mnemonicSeed")
        write(mnemonicSeed)
        clickOn("OK")
        sleep(TIMEOUT, SECONDS)
    }

    protected void importWatchOnlyWallet(String name, String extendedPubKeysJson) {
        clickOn("Import")
        clickOn("Watch-only Wallet")
        clickOn("#walletName")
        write(name)
        clickOn("#extendedPubkeys")
        write(extendedPubKeysJson)
        clickOn("OK")
        sleep(TIMEOUT, SECONDS)
    }

    protected void exportExtendedPubkeys() {
        clickOn("Export")
        clickOn("Extended Pubkeys")
        sleep(TIMEOUT, SECONDS)
    }

    FxRobot write(String text) {
        Platform.runLater(() -> {
            Copy.copy(text)
            press(KeyCode.CONTROL)
            press(KeyCode.V)
            release(KeyCode.CONTROL)
            release(KeyCode.V)
        })
        sleep(300, MILLISECONDS)
        null
    }
}
