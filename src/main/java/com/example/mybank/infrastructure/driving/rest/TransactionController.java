package com.example.mybank.infrastructure.driving.rest;

import com.example.mybank.domain.model.Account;
import com.example.mybank.domain.model.Amount;
import com.example.mybank.domain.model.Client;
import com.example.mybank.domain.model.Transaction.Type;
import com.example.mybank.domain.usecase.transaction.CreateTransaction;
import com.example.mybank.domain.usecase.transaction.ListTransactions;
import com.example.mybank.domain.usecase.transaction.StreamTransactions;
import com.example.mybank.infrastructure.driving.rest.dto.CreateTransactionRequest;
import com.example.mybank.infrastructure.driving.rest.dto.TransactionDTO;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

import static com.example.mybank.infrastructure.driving.rest.security.JwtTokenService.extractClientId;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final CreateTransaction createTransaction;
    private final ListTransactions listTransactions;
    private final StreamTransactions streamTransactions;

    public TransactionController(CreateTransaction createTransaction, ListTransactions listTransactions, StreamTransactions streamTransactions) {
        this.createTransaction = createTransaction;
        this.listTransactions = listTransactions;
        this.streamTransactions = streamTransactions;
    }

    @GetMapping(params = "accountId")
    public List<TransactionDTO> listByAccount(@RequestParam String accountId) {
        Client.Id clientId = extractClientId();
        return listTransactions.forAccount(Account.Id.from(accountId), clientId).stream().map(TransactionDTO::fromDomain).toList();
    }

    @PostMapping
    public TransactionDTO create(@RequestBody @Valid CreateTransactionRequest request) {
        Type type = Type.valueOf(request.type());
        var created = createTransaction.create(Account.Id.from(request.accountId()), Amount.fromCents(request.amountCents()), type, request.date());
        return TransactionDTO.fromDomain(created);
    }

    @GetMapping(value = "/stream", params = "accountId", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<TransactionDTO> stream(@RequestParam String accountId) {
        Client.Id clientId = extractClientId();
        return streamTransactions.forAccount(Account.Id.from(accountId), clientId).map(TransactionDTO::fromDomain);
    }
}
