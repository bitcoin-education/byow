package com.byow.wallet.byow.gui

import com.byow.wallet.byow.gui.controllers.ReceiveTabController
import javafx.fxml.FXMLLoader
import javafx.fxml.JavaFXBuilderFactory
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.stage.Stage
import javafx.util.Builder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.core.io.Resource
import org.testfx.api.FxToolkit
import org.testfx.framework.spock.ApplicationSpec

@SpringBootTest
class CreateWalletTest extends ApplicationSpec {

    @Value("classpath:/fxml/main_window.fxml")
    private Resource fxml

    @Autowired
    private ApplicationContext context

    private Stage stage

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
        this.stage = stage
        FXMLLoader fxmlLoader = new FXMLLoader(fxml.URL)
        fxmlLoader.controllerFactory = context::getBean
        fxmlLoader.builderFactory = type -> {
            if (type.equals(ReceiveTabController.class)) {
                return new Builder<Object>() {
                    @Override
                    Object build() {
                        return context.getBean(type)
                    }
                }
            }
            JavaFXBuilderFactory defaultFactory = new JavaFXBuilderFactory()
            return defaultFactory.getBuilder(type)
        }
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
