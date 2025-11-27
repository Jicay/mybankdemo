package com.example.mybank.domain.ports;


import com.example.mybank.domain.model.Client;
import com.example.mybank.domain.model.Client.Name;

import java.util.List;

public interface ClientRepository {
    List<Client> findAll();

    Client add(Client client);

    boolean existsBy(Name lastName, Name firstName);

    boolean existsById(Client.Id clientId);
}

