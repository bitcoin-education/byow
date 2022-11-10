package com.byow.wallet.byow.gui

import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.TextArea
import javafx.stage.Stage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.core.io.Resource
import org.testfx.api.FxToolkit
import org.testfx.framework.spock.ApplicationSpec

@SpringBootTest
class CreateWalletTest extends ApplicationSpec {
    @Autowired
    protected ApplicationContext context

    @Value("classpath:/fxml/main_window.fxml")
    protected Resource fxml

    @Override
    void init() throws Exception {
        FxToolkit.registerStage { new Stage() }
    }

    @Override
    void stop() throws Exception {
        FxToolkit.hideStage()
    }

    @Override
    void start(Stage stage) {
        FXMLLoader fxmlLoader = new FXMLLoader(fxml.getURL())
        fxmlLoader.controllerFactory = context::getBean
        Parent root = fxmlLoader.load()
        stage.title = "BYOW Wallet"
        stage.scene = new Scene(root)
        stage.show()
    }

    def "should create wallet"() {
        when:
            clickOn("New")
            clickOn("Wallet")
            clickOn("#name")
            write("My Test Wallet")
            clickOn("create")
            def mnemonicSeed = lookup("#mnemonicSeed").queryAs(TextArea.class).text
        then:
            mnemonicSeed
    }
}