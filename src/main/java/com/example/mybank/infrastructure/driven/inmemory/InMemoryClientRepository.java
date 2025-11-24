package com.example.mybank.infrastructure.driven.inmemory;

import com.example.mybank.domain.model.Client;
import com.example.mybank.domain.model.Client.Id;
import com.example.mybank.domain.model.Client.Name;
import com.example.mybank.domain.ports.ClientRepository;
import com.github.f4b6a3.ulid.Ulid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class InMemoryClientRepository implements ClientRepository {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryClientRepository.class);
    private final List<Client> clients;

    public InMemoryClientRepository() {
        clients = new ArrayList<>();
        clients.add(new Client(new Id(Ulid.fast()), new Name("Dupont"), new Name("Jean")));
        clients.add(new Client(new Id(Ulid.fast()), new Name("Martin"), new Name("Sophie")));
        logger.debug("Repository initialized with {} clients", clients.size());
    }

    @Override
    public List<Client> findAll() {
        logger.debug("Retrieving all clients");
        return new ArrayList<>(clients);
    }

    @Override
    public Client add(Client client) {
        Client toStore = client;
        if (toStore.id() == null) {
            toStore = new Client(new Id(Ulid.fast()), client.lastName(), client.firstName());
        }
        clients.add(toStore);
        logger.debug("Saved client {} {} with id {}", toStore.firstName(), toStore.lastName(), toStore.id());
        return toStore;
    }

    @Override
    public boolean existsBy(Name lastName, Name firstName) {
        return clients.stream().anyMatch(c -> c.lastName().value().equalsIgnoreCase(lastName.value())
                && c.firstName().value().equalsIgnoreCase(firstName.value()));
    }
}

