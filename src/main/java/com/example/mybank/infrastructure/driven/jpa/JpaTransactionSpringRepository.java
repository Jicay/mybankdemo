package com.example.mybank.infrastructure.driven.jpa;

import com.example.mybank.infrastructure.driven.jpa.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaTransactionSpringRepository extends JpaRepository<TransactionEntity, String> {
    List<TransactionEntity> findByAccountId(String accountId);
}
