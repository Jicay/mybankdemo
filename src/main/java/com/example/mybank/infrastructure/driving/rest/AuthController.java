package com.example.mybank.infrastructure.driving.rest;

import com.example.mybank.domain.model.Client.Name;
import com.example.mybank.domain.model.User.Login;
import com.example.mybank.domain.model.User.Password;
import com.example.mybank.domain.usecase.user.CreateUser;
import com.example.mybank.domain.usecase.user.FindUser;
import com.example.mybank.infrastructure.driving.rest.dto.AuthResponse;
import com.example.mybank.infrastructure.driving.rest.dto.LoginRequest;
import com.example.mybank.infrastructure.driving.rest.dto.RegisterRequest;
import com.example.mybank.infrastructure.driving.rest.security.JwtTokenService;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final CreateUser createUser;
    private final FindUser findUser;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwt;

    public AuthController(CreateUser createUser, PasswordEncoder passwordEncoder, JwtTokenService jwt, FindUser findUser) {
        this.createUser = createUser;
        this.passwordEncoder = passwordEncoder;
        this.jwt = jwt;
        this.findUser = findUser;
    }

    @ResponseStatus(CREATED)
    @PostMapping("/register")
    public AuthResponse register(@RequestBody @Valid RegisterRequest request) {
        var user = createUser.create(
                new Login(request.login()),
                new Password(passwordEncoder.encode(request.password())),
                new Name(request.lastName()),
                new Name(request.firstName())
        );
        String token = jwt.generate(request.login().toLowerCase(), Map.of("login", request.login().toLowerCase(), "clientId", user.clientId().value().toString()));
        return new AuthResponse(token, request.login().toLowerCase(), user.clientId().value().toString());
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        var user = findUser.by(new Login(request.login()), new Password(request.password()));
        String token = jwt.generate(user.login().value(), Map.of("login", user.login().value(), "clientId", user.clientId().value().toString()));
        return new AuthResponse(token, user.login().value(), user.clientId().value().toString());
    }
}
