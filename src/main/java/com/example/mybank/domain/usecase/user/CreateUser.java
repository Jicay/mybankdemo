package com.example.mybank.domain.usecase.user;

import com.example.mybank.domain.model.Client.Name;
import com.example.mybank.domain.model.User;
import com.example.mybank.domain.model.User.Login;
import com.example.mybank.domain.model.User.Password;
import com.example.mybank.domain.ports.UserRepository;
import com.example.mybank.domain.usecase.client.CreateClient;

public class CreateUser {
    private final UserRepository userRepository;
    private final CreateClient createClient;

    public CreateUser(UserRepository userRepository, CreateClient createClient) {
        this.userRepository = userRepository;
        this.createClient = createClient;
    }

    public User create(Login login, Password password, Name lastName, Name firstName) {
        if (userRepository.existsByLoginIgnoreCase(login)) {
            throw new UserAlreadyExistsException(login);
        }
        var client = createClient.create(lastName, firstName);
        var userId = new User.Id();

        return userRepository.add(new User(userId, login, password, client.id()));
    }

    public static class UserAlreadyExistsException extends RuntimeException {
        public UserAlreadyExistsException(Login login) {
            super("User already exists: " + login.value());
        }
    }
}
