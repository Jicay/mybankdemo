package com.example.mybank.domain.usecase.client;

import com.example.mybank.domain.model.Client;
import com.example.mybank.domain.ports.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ListClients {
    private static final Logger logger = LoggerFactory.getLogger(ListClients.class);
    private final ClientRepository clientRepository;

    public ListClients(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> all() {
        logger.debug("Listing all clients");
        return clientRepository.findAll();
    }
}
