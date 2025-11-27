package com.example.mybank.infrastructure.driven.jpa;

import com.example.mybank.domain.model.Account;
import com.example.mybank.domain.model.Account.Id;
import com.example.mybank.domain.model.Account.Name;
import com.example.mybank.domain.model.Account.Type;
import com.example.mybank.domain.model.Amount;
import com.example.mybank.domain.model.Client;
import com.example.mybank.domain.ports.AccountRepository;
import com.example.mybank.infrastructure.driven.jpa.entity.AccountEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaAccountRepository implements AccountRepository {
    private final JpaAccountSpringRepository springRepository;

    public JpaAccountRepository(JpaAccountSpringRepository springRepository) {
        this.springRepository = springRepository;
    }

    @Override
    public Account save(Account account) {
        AccountEntity entity = toEntity(account);
        AccountEntity saved = springRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<Account> findByClientId(Client.Id clientId) {
        return springRepository.findByClientId(clientId.value().toString())
                .stream().map(this::toDomain).toList();
    }

    private AccountEntity toEntity(Account account) {
        AccountEntity entity = new AccountEntity();
        entity.setId(account.id().value().toString());
        entity.setClientId(account.clientId().value().toString());
        entity.setName(account.name().value());
        entity.setType(AccountEntity.Type.valueOf(account.type().name()));
        entity.setAmountCents(account.amount().value());
        return entity;
    }

    private Account toDomain(AccountEntity entity) {
        return new Account(
                Id.from(entity.getId()),
                Client.Id.from(entity.getClientId()),
                new Name(entity.getName()),
                Type.valueOf(entity.getType().name()),
                Amount.fromCents(entity.getAmountCents())
        );
    }
}
