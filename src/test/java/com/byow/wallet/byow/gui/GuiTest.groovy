package com.byow.wallet.byow.gui

import com.byow.wallet.byow.gui.controllers.ReceiveTabController
import javafx.fxml.FXMLLoader
import javafx.fxml.JavaFXBuilderFactory
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.util.Builder
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.core.io.Resource
import org.testfx.api.FxToolkit
import org.testfx.framework.spock.ApplicationSpec

import java.security.Security

@SpringBootTest
abstract class GuiTest extends ApplicationSpec {
    @Value("classpath:/fxml/main_window.fxml")
    protected Resource fxml

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
        Security.addProvider(new BouncyCastleProvider());
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
}
