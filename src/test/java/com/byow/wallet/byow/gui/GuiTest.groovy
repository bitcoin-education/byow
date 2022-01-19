package com.byow.wallet.byow.gui

import com.byow.wallet.byow.gui.events.GuiStartedEvent
import javafx.stage.Stage
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.testfx.api.FxToolkit
import org.testfx.framework.spock.ApplicationSpec

import java.security.Security

@SpringBootTest
abstract class GuiTest extends ApplicationSpec {

    @Autowired
    protected ApplicationContext context

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
        this.stage = stage
        this.context.publishEvent(new GuiStartedEvent(this, stage));
    }

}
