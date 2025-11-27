package com.example.mybank.domain.ports;

import com.example.mybank.domain.model.Account;
import com.example.mybank.domain.model.Client;

import java.util.List;

public interface AccountRepository {
    Account save(Account account);

    List<Account> findByClientId(Client.Id clientId);
}
