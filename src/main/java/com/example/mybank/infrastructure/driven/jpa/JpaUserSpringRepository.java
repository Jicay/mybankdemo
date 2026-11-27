package com.example.mybank.infrastructure.driven.jpa;

import com.example.mybank.infrastructure.driven.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaUserSpringRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByLoginIgnoreCase(String login);

    boolean existsByLoginIgnoreCase(String login);
}
