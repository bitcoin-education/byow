package com.byow.wallet.byow.gui.events;

import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

public class GuiStartedEvent extends ApplicationEvent {
    private final Stage stage;

    public GuiStartedEvent(Object guiApplication, Stage stage) {
        super(guiApplication);
        this.stage = stage;
    }

    public Stage getStage() {
        return this.stage;
    }
}
