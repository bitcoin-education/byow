package com.byow.wallet.byow;

import com.byow.wallet.byow.gui.events.GuiStartedEvent;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class GuiApplication extends Application {
    private ConfigurableApplicationContext context;

    @Override
    public void init() throws Exception {
        this.context = SpringApplication.run(ByowApplication.class);
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.context.publishEvent(new GuiStartedEvent(this, stage));
    }
}
