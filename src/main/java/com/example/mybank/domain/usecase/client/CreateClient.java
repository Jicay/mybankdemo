package com.example.mybank.domain.usecase.client;

import com.example.mybank.domain.model.Client;
import com.example.mybank.domain.model.Client.Name;
import com.example.mybank.domain.ports.ClientRepository;
import com.github.f4b6a3.ulid.Ulid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateClient {
    private static final Logger logger = LoggerFactory.getLogger(CreateClient.class);
    private final ClientRepository clientRepository;

    public CreateClient(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client create(Name lastName, Name firstName) {
        if (clientRepository.existsBy(lastName, firstName)) {
            throw new ClientAlreadyExistsException(lastName, firstName);
        }
        Client client = new Client(new Client.Id(Ulid.fast()), lastName, firstName);
        Client saved = clientRepository.add(client);
        logger.debug("Created client {} {} with id {}", saved.firstName(), saved.lastName(), saved.id());
        return saved;
    }

    public static class ClientAlreadyExistsException extends RuntimeException {
        public ClientAlreadyExistsException(Name lastName, Name firstName) {
            super("Client already exists: " + lastName.value() + " " + firstName.value());
        }
    }
}
