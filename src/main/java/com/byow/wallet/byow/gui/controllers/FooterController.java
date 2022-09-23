package com.byow.wallet.byow.gui.controllers;

import com.byow.wallet.byow.observables.AsyncProgress;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FooterController extends Label {

    private final AsyncProgress asyncProgress;

    public FooterController(
        @Value("fxml/footer.fxml") Resource fxml,
        ApplicationContext context,
        AsyncProgress asyncProgress
    ) throws IOException {
        this.asyncProgress = asyncProgress;
        FXMLLoader fxmlLoader = new FXMLLoader(
            fxml.getURL(),
            null,
            null,
            context::getBean
        );
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
    }

    public void initialize() {
        textProperty().bind(asyncProgress.taskDescriptionProperty());
    }
}
