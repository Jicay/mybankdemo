package com.example.mybank.infrastructure.driven.kafka;

import com.example.mybank.infrastructure.messaging.TransactionEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
@Profile("producer")
public class TransactionProducer {
    private final Sinks.Many<TransactionEvent> sink = Sinks.many().multicast().directBestEffort();

    public Flux<TransactionEvent> getFlux() {
        return sink.asFlux();
    }

    public void produce(TransactionEvent value) {
        sink.tryEmitNext(value);
    }
}
