package com.byow.wallet.byow.node.tasks;

import com.byow.wallet.byow.node.events.TransactionReceivedEvent;
import io.github.bitcoineducation.bitcoinjava.Transaction;
import javafx.concurrent.Task;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.zeromq.ZMQ.Socket;

import java.io.ByteArrayInputStream;

import static java.lang.Thread.currentThread;

@Service
public class NodeTask extends Task<Void> {
    private final Socket subscriber;

    private final String zmqUrl;

    private final ApplicationEventPublisher applicationEventPublisher;

    public NodeTask(
        Socket subscriber,
        @Value("${zmq.url}") String zmqUrl,
        ApplicationEventPublisher applicationEventPublisher
    ) {
        this.subscriber = subscriber;
        this.zmqUrl = zmqUrl;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    protected Void call() throws Exception {
        subscriber.setReceiveTimeOut(1000);
        subscriber.subscribe("rawtx".getBytes());
        subscriber.connect(zmqUrl);

        while (!currentThread().isInterrupted()) {
            System.out.println(1);
            if (isCancelled()) {
                break;
            }
            String topic = subscriber.recvStr();
            if (!"rawtx".equals(topic)) {
                continue;
            }
            byte[] contents = subscriber.recv();

            Transaction transaction = Transaction.fromByteStream(new ByteArrayInputStream(contents));

            applicationEventPublisher.publishEvent(
                new TransactionReceivedEvent(this, transaction)
            );
        }

        return null;
    }
}
