package com.example.mybank.domain.usecase.transaction;

import com.example.mybank.domain.model.Account;
import com.example.mybank.domain.model.Client;
import com.example.mybank.domain.model.Transaction;
import com.example.mybank.domain.ports.AccountRepository;
import com.example.mybank.domain.ports.TransactionRepository;

import java.util.List;

public class ListTransactions {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public ListTransactions(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    public List<Transaction> forAccount(Account.Id accountId, Client.Id clientId) {
        var account = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException(accountId));
        if (!account.clientId().equals(clientId)) {
            throw new ClientNotAuthorizedException(accountId, clientId);
        }
        return transactionRepository.findByAccountId(accountId);
    }

    public static class AccountNotFoundException extends RuntimeException {
        public AccountNotFoundException(Account.Id accountId) {
            super("Account not found: " + accountId.value().toString());
        }
    }

    public static class ClientNotAuthorizedException extends RuntimeException {
        public ClientNotAuthorizedException(Account.Id accountId, Client.Id clientId) {
            super("Client not authorized: " + accountId.value().toString() + " for client " + clientId.value().toString());
        }
    }
}
