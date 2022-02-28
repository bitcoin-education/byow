package com.byow.wallet.byow.gui

import com.byow.wallet.byow.api.services.ExtendedPubkeyService
import com.byow.wallet.byow.api.services.SegwitAddressGenerator
import com.byow.wallet.byow.api.services.node.NodeLoadOrCreateWalletService
import com.byow.wallet.byow.api.services.node.client.NodeGenerateToAddressClient
import com.byow.wallet.byow.api.services.node.client.NodeGetBalanceClient
import com.byow.wallet.byow.api.services.node.client.NodeGetNewAddressClient
import com.byow.wallet.byow.api.services.node.client.NodeSendToAddressClient
import com.byow.wallet.byow.domains.AddressType
import com.byow.wallet.byow.gui.events.GuiStartedEvent
import io.github.bitcoineducation.bitcoinjava.ExtendedKeyPrefixes
import io.github.bitcoineducation.bitcoinjava.ExtendedPrivateKey
import io.github.bitcoineducation.bitcoinjava.ExtendedPubkey
import io.github.bitcoineducation.bitcoinjava.MnemonicSeed
import javafx.scene.control.TableView
import javafx.stage.Stage
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.testfx.api.FxToolkit
import org.testfx.framework.spock.ApplicationSpec

import java.security.Security

import static java.util.concurrent.TimeUnit.SECONDS
import static org.testfx.util.WaitForAsyncUtils.waitFor

@SpringBootTest
@ActiveProfiles('test')
abstract class GuiTest extends ApplicationSpec {

    protected static final String TESTWALLET = "testwallet"

    protected static final int TIMEOUT = 10

    @Autowired
    protected ApplicationContext context

    protected Stage stage

    @Autowired
    private NodeLoadOrCreateWalletService nodeLoadOrCreateWalletService

    @Autowired
    private NodeGetBalanceClient nodeGetBalanceClient

    @Autowired
    protected NodeGetNewAddressClient nodeGetNewAddressClient

    @Autowired
    protected NodeGenerateToAddressClient nodeGenerateToAddressClient

    @Autowired
    private NodeSendToAddressClient nodeSendToAddressClient

    @Autowired
    ExtendedPubkeyService extendedPubkeyService

    @Autowired
    SegwitAddressGenerator segwitAddressGenerator

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
        this.stage = stage
        this.context.publishEvent(new GuiStartedEvent(this, stage));
    }

    def loadWalletAndAddBalance() {
        nodeLoadOrCreateWalletService.loadOrCreateWallet(TESTWALLET)
        createBalanceIfNecessary()
    }

    private void createBalanceIfNecessary() {
        double balance = nodeGetBalanceClient.get(TESTWALLET)
        if (balance < 50) {
            String address = nodeGetNewAddressClient.getNewAddress(TESTWALLET)
            nodeGenerateToAddressClient.generateToAddress(TESTWALLET, 150, address)
        }
    }

    protected void sendBitcoinAndWait(String address, String expectedTotalAmount = "1.0", int expectedTotalSize = 1, String lookupElement = "#addressesTable") {
        nodeSendToAddressClient.sendToAddress(TESTWALLET, address, 1.0)
        waitFor(TIMEOUT, SECONDS, {
            TableView tableView = lookup(lookupElement).queryAs(TableView)
            return tableView.items.size() == expectedTotalSize && tableView.items[expectedTotalSize - 1].balance == expectedTotalAmount
        })
    }

    protected boolean addressIsValid(String address, String mnemonicSeedString, Integer index) {
        MnemonicSeed mnemonicSeed = new MnemonicSeed(mnemonicSeedString)
        ExtendedPrivateKey masterKey = mnemonicSeed.toMasterKey("", ExtendedKeyPrefixes.MAINNET_PREFIX.getPrivatePrefix())
        String extendedPubkeyString = extendedPubkeyService.create(masterKey, "84'/0'/0'/0/".concat(index.toString()), AddressType.SEGWIT).getKey()
        ExtendedPubkey extendedPubkey = ExtendedPubkey.unserialize(extendedPubkeyString)
        String expectedAddress = segwitAddressGenerator.generate(extendedPubkey)
        return expectedAddress == address
    }
}
