package com.example.mybank.infrastructure.driving.rest;

import com.example.mybank.domain.model.Account;
import com.example.mybank.domain.model.Amount;
import com.example.mybank.domain.model.Client;
import com.example.mybank.domain.usecase.account.CreateAccount;
import com.example.mybank.domain.usecase.account.ListAccounts;
import com.example.mybank.infrastructure.driving.rest.dto.AccountDTO;
import com.example.mybank.infrastructure.driving.rest.dto.CreateAccountRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.mybank.infrastructure.driving.rest.security.JwtTokenService.extractClientId;

@RestController
@RequestMapping("/api/clients/accounts")
public class AccountController {
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
    private final ListAccounts listAccounts;
    private final CreateAccount createAccount;

    public AccountController(ListAccounts listAccounts, CreateAccount createAccount) {
        this.listAccounts = listAccounts;
        this.createAccount = createAccount;
    }

    @GetMapping
    public List<AccountDTO> list() {
        Client.Id clientId = extractClientId();
        logger.info("Appel GET /clients/accounts for {}", clientId);
        var accounts = listAccounts.forClient(clientId);
        return accounts.stream().map(AccountDTO::fromDomain).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountDTO create(@RequestBody @Valid CreateAccountRequest request) {
        Client.Id clientId = extractClientId();
        logger.info("Appel POST /clients/accounts for {}", clientId);
        Account saved = createAccount.create(clientId, new Account.Name(request.name()), Account.Type.valueOf(request.type()), Amount.fromCents(request.amountCents()));
        return AccountDTO.fromDomain(saved);
    }
}
