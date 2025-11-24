package com.example.mybank.infrastructure.driving.rest;

import com.example.mybank.domain.model.Client.Name;
import com.example.mybank.domain.usecase.client.CreateClient;
import com.example.mybank.domain.usecase.client.ListClients;
import com.example.mybank.infrastructure.driving.rest.GlobalExceptionHandler.ErrorResponse;
import com.example.mybank.infrastructure.driving.rest.dto.ClientDTO;
import com.example.mybank.infrastructure.driving.rest.dto.CreateClientRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Clients", description = "Manage clients")
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
    @Operation(summary = "List all clients")
    @ApiResponse(responseCode = "200", description = "List of all clients")
    public List<ClientDTO> getAllClients() {
        logger.info("Appel GET /clients");
        return listClients.all().stream().map(ClientDTO::fromDomain).toList();
    }

    @PostMapping
    @Operation(summary = "Create a client")
    @ApiResponse(responseCode = "201", description = "Client created")
    @ApiResponse(responseCode = "400", description = "Invalid data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Client already exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ResponseStatus(HttpStatus.CREATED)
    public ClientDTO create(@RequestBody @Valid CreateClientRequest dto) {
        logger.info("Appel POST /clients");
        var saved = createClient.create(new Name(dto.lastName()), new Name(dto.firstName()));
        return ClientDTO.fromDomain(saved);
    }
}
