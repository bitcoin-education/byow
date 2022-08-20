package com.byow.wallet.byow.node;

import com.byow.wallet.byow.node.events.BlockReceivedEvent;
import com.byow.wallet.byow.node.events.TransactionReceivedEvent;
import io.github.bitcoineducation.bitcoinjava.Transaction;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.zeromq.ZMQ.Socket;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import static java.lang.Thread.currentThread;

@Service
public class NodeTask {
    private final Socket subscriber;

    private final String zmqUrl;

    private final ApplicationEventPublisher applicationEventPublisher;

    private static final Logger logger = LoggerFactory.getLogger(NodeTask.class);

    public NodeTask(
        Socket subscriber,
        @Value("${zmq.url}") String zmqUrl,
        ApplicationEventPublisher applicationEventPublisher
    ) {
        this.subscriber = subscriber;
        this.zmqUrl = zmqUrl;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Async("nodeExecutorService")
    public void run() throws IOException {
        subscriber.setReceiveTimeOut(1000);
        subscriber.subscribe("rawtx".getBytes());
        subscriber.subscribe("hashblock".getBytes());
        subscriber.connect(zmqUrl);

        while(!currentThread().isInterrupted()) {
            String topic = subscriber.recvStr();
            if(topic == null || !List.of("rawtx", "hashblock").contains(topic)) {
                continue;
            }
            byte[] contents = subscriber.recv();
            switch (topic) {
                case "rawtx" -> {
                    Transaction transaction;
                    try {
                        transaction = Transaction.fromByteStream(new ByteArrayInputStream(contents));
                        applicationEventPublisher.publishEvent(new TransactionReceivedEvent(this, transaction));
                    } catch (Exception e) {
                        logger.warn("Unable to parse content {}", Hex.toHexString(contents), e);
                    }
                }
                case "hashblock" -> applicationEventPublisher.publishEvent(new BlockReceivedEvent(this));
            }

        }
    }
}
