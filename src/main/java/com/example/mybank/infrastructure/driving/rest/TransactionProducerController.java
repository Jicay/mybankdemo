package com.example.mybank.infrastructure.driving.rest;

import com.example.mybank.infrastructure.driven.kafka.TransactionProducer;
import com.example.mybank.infrastructure.driving.rest.dto.CreateTransactionRequest;
import com.example.mybank.infrastructure.messaging.TransactionEvent;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;

@RestController
@RequestMapping("/api/producer/transactions")
@Profile("producer")
public class TransactionProducerController {

    private final TransactionProducer transactionProducer;

    public TransactionProducerController(TransactionProducer transactionProducer) {
        this.transactionProducer = transactionProducer;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void create(@RequestBody @Valid CreateTransactionRequest request) {
        var event = new TransactionEvent(
                request.accountId(),
                request.amountCents(),
                request.type(),
                Clock.systemUTC().instant()
        );

        transactionProducer.produce(event);
    }
}
