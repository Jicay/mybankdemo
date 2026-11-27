package com.example.mybank.domain.usecase.transaction;

import com.example.mybank.domain.model.Account;
import com.example.mybank.domain.model.Amount;
import com.example.mybank.domain.model.Transaction;
import com.example.mybank.domain.ports.TransactionRepository;
import reactor.core.publisher.Sinks;

import java.time.Instant;

public class CreateTransaction {
    private final TransactionRepository transactionRepository;
    private final Sinks.Many<Transaction> sink;

    public CreateTransaction(TransactionRepository transactionRepository, Sinks.Many<Transaction> sink) {
        this.transactionRepository = transactionRepository;
        this.sink = sink;
    }

    public Transaction create(Account.Id accountId, Amount amount, Transaction.Type type, Instant date) {
        var tx = new Transaction(new Transaction.Id(), accountId, amount, type, date == null ? Instant.now() : date);
        var saved = transactionRepository.save(tx);
        sink.tryEmitNext(saved);
        return saved;
    }
}
