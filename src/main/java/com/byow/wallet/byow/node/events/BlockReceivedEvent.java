package com.byow.wallet.byow.node.events;

import com.byow.wallet.byow.node.NodeTask;
import org.springframework.context.ApplicationEvent;

public class BlockReceivedEvent extends ApplicationEvent {
    public BlockReceivedEvent(NodeTask nodeTask) {
        super(nodeTask);
    }
}
