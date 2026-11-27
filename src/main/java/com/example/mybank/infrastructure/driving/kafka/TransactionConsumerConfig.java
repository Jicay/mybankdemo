package com.example.mybank.infrastructure.driving.kafka;

import com.example.mybank.domain.model.Account;
import com.example.mybank.domain.model.Amount;
import com.example.mybank.domain.model.Transaction;
import com.example.mybank.domain.usecase.transaction.CreateTransaction;
import com.example.mybank.infrastructure.messaging.TransactionEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.function.Consumer;

@Configuration
@Profile("consumer")
public class TransactionConsumerConfig {

    @Bean
    public Consumer<TransactionEvent> transactions(CreateTransaction createTransaction) {
        return event -> createTransaction.create(
                Account.Id.from(event.accountId()),
                Amount.fromCents(event.amountCents()),
                Transaction.Type.valueOf(event.type()),
                event.date()
        );
    }
}
