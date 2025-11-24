package com.example.mybank.infrastructure.driving.rest;

import com.example.mybank.domain.model.Client.Name;
import com.example.mybank.domain.usecase.client.CreateClient;
import com.example.mybank.domain.usecase.client.ListClients;
import com.example.mybank.infrastructure.driving.rest.dto.ClientDTO;
import com.example.mybank.infrastructure.driving.rest.dto.CreateClientRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {
    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);
    private final ListClients listClients;
    private final CreateClient createClient;

    public ClientController(ListClients listClients, CreateClient createClient) {
        this.listClients = listClients;
        this.createClient = createClient;
    }

    @GetMapping
    public List<ClientDTO> getAllClients() {
        logger.info("Appel GET /clients");
        return listClients.all().stream().map(ClientDTO::fromDomain).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientDTO create(@RequestBody CreateClientRequest dto) {
        logger.info("Appel POST /clients");
        var saved = createClient.create(new Name(dto.lastName()), new Name(dto.firstName()));
        return ClientDTO.fromDomain(saved);
    }
}
