package com.example.mybank.infrastructure.driven.jpa;

import com.example.mybank.infrastructure.driven.jpa.entity.TransactionEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Profile("consumer")
public interface JpaTransactionSpringRepository extends JpaRepository<TransactionEntity, String> {
    List<TransactionEntity> findByAccountId(String accountId);
}
