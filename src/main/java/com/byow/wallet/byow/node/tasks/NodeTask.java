package com.byow.wallet.byow.node.tasks;

import com.byow.wallet.byow.node.events.TransactionReceivedEvent;
import io.github.bitcoineducation.bitcoinjava.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.zeromq.ZMQ.Socket;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static java.lang.Thread.currentThread;

@Service
public class NodeTask {
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

    @Async("nodeExecutorService")
    public void run() throws IOException {
        subscriber.setReceiveTimeOut(1000);
        subscriber.subscribe("rawtx".getBytes());
        subscriber.connect(zmqUrl);

        while (!currentThread().isInterrupted()) {
            String topic = subscriber.recvStr();
            if (!"rawtx".equals(topic)) {
                continue;
            }
            byte[] contents = subscriber.recv();

            Transaction transaction = Transaction.fromByteStream(new ByteArrayInputStream(contents));
            System.out.println(transaction.id());

            applicationEventPublisher.publishEvent(
                new TransactionReceivedEvent(this, transaction)
            );
        }
    }
}