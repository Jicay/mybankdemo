package com.example.mybank.infrastructure.driven.jpa;

import com.example.mybank.infrastructure.driven.jpa.entity.AccountEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Profile("consumer")
public interface JpaAccountSpringRepository extends JpaRepository<AccountEntity, String> {
    List<AccountEntity> findByClientId(String clientId);
}
