package com.example.mybank.infrastructure.driven.jpa;

import com.example.mybank.domain.model.Client;
import com.example.mybank.domain.model.User;
import com.example.mybank.domain.model.User.Id;
import com.example.mybank.domain.model.User.Login;
import com.example.mybank.domain.model.User.Password;
import com.example.mybank.domain.ports.UserRepository;
import com.example.mybank.infrastructure.driven.jpa.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaUserRepository implements UserRepository {
    private final JpaUserSpringRepository springRepository;

    public JpaUserRepository(JpaUserSpringRepository springRepository) {
        this.springRepository = springRepository;
    }

    private UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.id().value().toString());
        entity.setClientId(user.clientId().value().toString());
        entity.setLogin(user.login().value());
        entity.setPassword(user.password().value());
        return entity;
    }

    private User toDomain(UserEntity entity) {
        return new User(
                Id.from(entity.getId()),
                new Login(entity.getLogin()),
                new Password(entity.getPassword()),
                Client.Id.from(entity.getClientId())
        );
    }

    @Override
    public User add(User user) {
        return toDomain(springRepository.save(toEntity(user)));
    }

    @Override
    public boolean existsByLoginIgnoreCase(Login login) {
        return springRepository.existsByLoginIgnoreCase(login.value());
    }

    @Override
    public Optional<User> findByLoginIgnoreCase(Login login) {
        return springRepository.findByLoginIgnoreCase(login.value()).map(this::toDomain);
    }
}
