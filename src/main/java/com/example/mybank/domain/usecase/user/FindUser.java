package com.example.mybank.domain.usecase.user;

import com.example.mybank.domain.model.User;
import com.example.mybank.domain.model.User.Login;
import com.example.mybank.domain.model.User.Password;
import com.example.mybank.domain.ports.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

public class FindUser {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public FindUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User by(Login login, Password password) {
        var userOpt = userRepository.findByLoginIgnoreCase(login);
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException(login);
        }
        var user = userOpt.get();
        if (!passwordEncoder.matches(password.value(), user.password().value())) {
            throw new InvalidLoginException(login);
        }
        return user;
    }

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(Login login) {
            super("User not found: " + login.value());
        }
    }

    public static class InvalidLoginException extends RuntimeException {
        public InvalidLoginException(Login login) {
            super("Invalid login: " + login.value());
        }
    }
}
