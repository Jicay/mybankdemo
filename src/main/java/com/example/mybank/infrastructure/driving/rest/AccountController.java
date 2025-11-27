package com.example.mybank.infrastructure.driving.rest;

import com.example.mybank.domain.model.Account;
import com.example.mybank.domain.model.Amount;
import com.example.mybank.domain.model.Client;
import com.example.mybank.domain.usecase.account.CreateAccount;
import com.example.mybank.domain.usecase.account.ListAccounts;
import com.example.mybank.infrastructure.driving.rest.dto.AccountDTO;
import com.example.mybank.infrastructure.driving.rest.dto.CreateAccountRequest;
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
@Tag(name = "Clients", description = "Manage accounts of a client")
@RequestMapping("/api/clients/{clientId}/accounts")
public class AccountController {
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
    private final ListAccounts listAccounts;
    private final CreateAccount createAccount;

    public AccountController(ListAccounts listAccounts, CreateAccount createAccount) {
        this.listAccounts = listAccounts;
        this.createAccount = createAccount;
    }

    @GetMapping
    @Operation(summary = "List all accounts of a client")
    @ApiResponse(responseCode = "200", description = "List of all accounts of a client")
    public List<AccountDTO> list(@PathVariable String clientId) {
        logger.info("Appel GET /clients/{}/accounts", clientId);
        var accounts = listAccounts.forClient(Client.Id.from(clientId));
        return accounts.stream().map(AccountDTO::fromDomain).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create an account for a client")
    @ApiResponse(responseCode = "201", description = "Account created")
    @ApiResponse(responseCode = "400", description = "Invalid data",
            content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Client not found",
            content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Technical error",
            content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
    public AccountDTO create(@PathVariable String clientId, @RequestBody @Valid CreateAccountRequest request) {
        logger.info("Appel POST /clients/{}/accounts", clientId);
        Account saved = createAccount.create(Client.Id.from(clientId), new Account.Name(request.name()), Account.Type.valueOf(request.type()), Amount.fromCents(request.amountCents()));
        return AccountDTO.fromDomain(saved);
    }
}
