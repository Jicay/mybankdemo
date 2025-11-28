package com.example.mybank.infrastructure.application.config;

import com.example.mybank.domain.ports.AccountRepository;
import com.example.mybank.domain.ports.ClientRepository;
import com.example.mybank.domain.ports.UserRepository;
import com.example.mybank.domain.usecase.account.CreateAccount;
import com.example.mybank.domain.usecase.account.ListAccounts;
import com.example.mybank.domain.usecase.client.CreateClient;
import com.example.mybank.domain.usecase.client.ListClients;
import com.example.mybank.domain.usecase.user.CreateUser;
import com.example.mybank.domain.usecase.user.FindUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UseCaseConfiguration {
    @Bean
    public ListClients listClientsUseCase(ClientRepository clientRepository) {
        return new ListClients(clientRepository);
    }

    @Bean
    public CreateClient createClientUseCase(ClientRepository clientRepository) {
        return new CreateClient(clientRepository);
    }

    @Bean
    public ListAccounts listAccountsUseCase(AccountRepository accountRepository) {
        return new ListAccounts(accountRepository);
    }

    @Bean
    public CreateAccount createAccountUseCase(AccountRepository accountRepository, ClientRepository clientRepository) {
        return new CreateAccount(accountRepository, clientRepository);
    }

    @Bean
    public CreateUser createUserUseCase(UserRepository userRepository, CreateClient createClient) {
        return new CreateUser(userRepository, createClient);
    }

    @Bean
    public FindUser findUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return new FindUser(userRepository, passwordEncoder);
    }
}
