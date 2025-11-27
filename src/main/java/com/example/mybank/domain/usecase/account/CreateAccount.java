package com.example.mybank.domain.usecase.account;


import com.example.mybank.domain.model.Account;
import com.example.mybank.domain.model.Account.Name;
import com.example.mybank.domain.model.Account.Type;
import com.example.mybank.domain.model.Amount;
import com.example.mybank.domain.model.Client;
import com.example.mybank.domain.ports.AccountRepository;
import com.example.mybank.domain.ports.ClientRepository;

public class CreateAccount {
    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;

    public CreateAccount(AccountRepository accountRepository, ClientRepository clientRepository) {
        this.accountRepository = accountRepository;
        this.clientRepository = clientRepository;
    }

    public Account create(Client.Id clientId, Name name, Type type, Amount amount) {
        if (!clientRepository.existsById(clientId)) {
            throw new ClientNotFoundException(clientId);
        }
        Account toSave = new Account(new Account.Id(), clientId, name, type, amount);
        return accountRepository.save(toSave);
    }

    public static class ClientNotFoundException extends RuntimeException {
        public ClientNotFoundException(Client.Id clientId) {
            super("Client not found: " + clientId.value().toString());
        }
    }
}
