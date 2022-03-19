package com.byow.wallet.byow.gui.listeners;

import com.byow.wallet.byow.gui.controllers.AddressesTableController;
import com.byow.wallet.byow.gui.controllers.ReceiveTabController;
import com.byow.wallet.byow.gui.controllers.TotalBalanceController;
import com.byow.wallet.byow.gui.controllers.TransactionsTableController;
import com.byow.wallet.byow.gui.events.GuiStartedEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Builder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class GuiStartedListener implements ApplicationListener<GuiStartedEvent> {
    private static final Set<Class<?>> customComponents = Set.of(
        ReceiveTabController.class,
        AddressesTableController.class,
        TransactionsTableController.class,
        TotalBalanceController.class
    );

    private final Resource fxml;

    private final ApplicationContext context;

    public GuiStartedListener(
        @Value("classpath:/fxml/main_window.fxml") Resource fxml,
        ApplicationContext context
    ) {
        this.fxml = fxml;
        this.context = context;
    }

    @Override
    public void onApplicationEvent(GuiStartedEvent guiStartedEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Stage stage = guiStartedEvent.getStage();
        stage.setTitle("BYOW Wallet");
        stage.setScene(new Scene(initializeFxml(fxmlLoader)));
        stage.show();
    }

    private Parent initializeFxml(FXMLLoader fxmlLoader) {
        Parent root;
        try {
            fxmlLoader.setLocation(this.fxml.getURL());
            fxmlLoader.setControllerFactory(context::getBean);
            fxmlLoader.setBuilderFactory(type -> {
                if (customComponents.contains(type)) {
                    return (Builder<Object>) () -> context.getBean(type);
                }
                JavaFXBuilderFactory defaultFactory = new JavaFXBuilderFactory();
                return defaultFactory.getBuilder(type);
            });
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return root;
    }
}
