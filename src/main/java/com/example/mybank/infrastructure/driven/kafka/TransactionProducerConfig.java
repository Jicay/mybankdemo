package com.example.mybank.infrastructure.driven.kafka;

import com.example.mybank.infrastructure.messaging.TransactionEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import reactor.core.publisher.Flux;

import java.util.function.Supplier;

@Configuration
@Profile("producer")
public class TransactionProducerConfig {

    @Bean
    public Supplier<Flux<TransactionEvent>> transactions(TransactionProducer producer) {
        return producer::getFlux;
    }
}
