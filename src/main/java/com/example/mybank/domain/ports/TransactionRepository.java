package com.example.mybank.domain.ports;

import com.example.mybank.domain.model.Account;
import com.example.mybank.domain.model.Transaction;

import java.util.List;

public interface TransactionRepository {
    List<Transaction> findByAccountId(Account.Id accountId);

    Transaction save(Transaction transaction);
}
