package com.example.mybank.domain.ports;

import com.example.mybank.domain.model.User;
import com.example.mybank.domain.model.User.Login;

import java.util.Optional;

public interface UserRepository {
    User add(User user);

    boolean existsByLoginIgnoreCase(Login login);

    Optional<User> findByLoginIgnoreCase(Login login);
}

