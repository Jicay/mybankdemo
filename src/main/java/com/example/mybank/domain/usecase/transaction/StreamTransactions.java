package com.example.mybank.domain.usecase.transaction;

import com.example.mybank.domain.model.Account;
import com.example.mybank.domain.model.Client;
import com.example.mybank.domain.model.Transaction;
import com.example.mybank.domain.ports.AccountRepository;
import com.example.mybank.domain.usecase.transaction.ListTransactions.AccountNotFoundException;
import com.example.mybank.domain.usecase.transaction.ListTransactions.ClientNotAuthorizedException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

public class StreamTransactions {
    private final AccountRepository accountRepository;
    private final Sinks.Many<Transaction> sink;

    public StreamTransactions(AccountRepository accountRepository, Sinks.Many<Transaction> sink) {
        this.accountRepository = accountRepository;
        this.sink = sink;
    }

    public Flux<Transaction> forAccount(Account.Id accountId, Client.Id clientId) {
        var account = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException(accountId));
        if (!account.clientId().equals(clientId)) {
            throw new ClientNotAuthorizedException(accountId, clientId);
        }
        return sink.asFlux().filter(t -> t.accountId().equals(accountId));
    }
}
