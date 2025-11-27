package com.example.mybank.domain.usecase.account;

import com.example.mybank.domain.model.Account;
import com.example.mybank.domain.model.Client;
import com.example.mybank.domain.ports.AccountRepository;

import java.util.List;

public class ListAccounts {
    private final AccountRepository accountRepository;

    public ListAccounts(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> forClient(Client.Id clientId) {
        return accountRepository.findByClientId(clientId);
    }
}
