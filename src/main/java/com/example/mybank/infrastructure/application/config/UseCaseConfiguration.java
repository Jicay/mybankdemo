package com.example.mybank.infrastructure.application.config;

import com.example.mybank.domain.model.Transaction;
import com.example.mybank.domain.ports.AccountRepository;
import com.example.mybank.domain.ports.ClientRepository;
import com.example.mybank.domain.ports.TransactionRepository;
import com.example.mybank.domain.ports.UserRepository;
import com.example.mybank.domain.usecase.account.CreateAccount;
import com.example.mybank.domain.usecase.account.ListAccounts;
import com.example.mybank.domain.usecase.client.CreateClient;
import com.example.mybank.domain.usecase.client.ListClients;
import com.example.mybank.domain.usecase.transaction.CreateTransaction;
import com.example.mybank.domain.usecase.transaction.ListTransactions;
import com.example.mybank.domain.usecase.transaction.StreamTransactions;
import com.example.mybank.domain.usecase.user.CreateUser;
import com.example.mybank.domain.usecase.user.FindUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Sinks;

@Configuration
@Profile("consumer")
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

    @Bean
    public Sinks.Many<Transaction> transactionSink() {
        return Sinks.many().multicast().directBestEffort();
    }

    @Bean
    public CreateTransaction createTransactionUseCase(TransactionRepository transactionRepository, Sinks.Many<Transaction> transactionSink) {
        return new CreateTransaction(transactionRepository, transactionSink);
    }

    @Bean
    public ListTransactions listTransactionsUseCase(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        return new ListTransactions(transactionRepository, accountRepository);
    }

    @Bean
    public StreamTransactions streamTransactionsUseCase(AccountRepository accountRepository, Sinks.Many<Transaction> transactionSink) {
        return new StreamTransactions(accountRepository, transactionSink);
    }
}
