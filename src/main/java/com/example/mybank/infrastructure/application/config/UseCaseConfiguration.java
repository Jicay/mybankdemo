package com.example.mybank.infrastructure.application.config;

import com.example.mybank.domain.ports.ClientRepository;
import com.example.mybank.domain.usecase.client.CreateClient;
import com.example.mybank.domain.usecase.client.ListClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
