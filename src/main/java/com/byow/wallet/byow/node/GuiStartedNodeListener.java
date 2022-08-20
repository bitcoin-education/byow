package com.byow.wallet.byow.node;

import com.byow.wallet.byow.gui.events.GuiStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GuiStartedNodeListener implements ApplicationListener<GuiStartedEvent> {
    private final NodeTask nodeTask;

    public GuiStartedNodeListener(NodeTask nodeTask) {
        this.nodeTask = nodeTask;
    }

    @Override
    public void onApplicationEvent(GuiStartedEvent event) {
        try {
            nodeTask.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
