package com.example.mybank.infrastructure.driven.jpa;

import com.example.mybank.domain.model.Account;
import com.example.mybank.domain.model.Amount;
import com.example.mybank.domain.model.Transaction;
import com.example.mybank.domain.ports.TransactionRepository;
import com.example.mybank.infrastructure.driven.jpa.entity.TransactionEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaTransactionRepository implements TransactionRepository {

    private final JpaTransactionSpringRepository springRepository;

    public JpaTransactionRepository(JpaTransactionSpringRepository springRepository) {
        this.springRepository = springRepository;
    }

    @Override
    public List<Transaction> findByAccountId(Account.Id accountId) {
        return springRepository.findByAccountId(accountId.value().toString()).stream().map(this::toDomain).toList();
    }

    @Override
    public Transaction save(Transaction transaction) {
        var saved = springRepository.save(toEntity(transaction));
        return toDomain(saved);
    }

    private Transaction toDomain(TransactionEntity e) {
        return new Transaction(
                Transaction.Id.from(e.getId()),
                Account.Id.from(e.getAccountId()),
                Amount.fromCents(e.getAmountCents()),
                switch (e.getType()) {
                    case DEBIT -> Transaction.Type.DEBIT;
                    case CREDIT -> Transaction.Type.CREDIT;
                },
                e.getDate()
        );
    }

    private TransactionEntity toEntity(Transaction t) {
        var e = new TransactionEntity();
        e.setId(t.id().value().toString());
        e.setAccountId(t.accountId().value().toString());
        e.setAmountCents(t.amount().value());
        e.setType(switch (t.type()) {
            case DEBIT -> TransactionEntity.Type.DEBIT;
            case CREDIT -> TransactionEntity.Type.CREDIT;
        });
        e.setDate(t.date());
        return e;
    }
}
