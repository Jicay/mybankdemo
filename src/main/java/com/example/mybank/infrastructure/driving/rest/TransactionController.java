package com.example.mybank.infrastructure.driving.rest;

import com.example.mybank.domain.model.Account;
import com.example.mybank.domain.model.Client;
import com.example.mybank.domain.usecase.transaction.ListTransactions;
import com.example.mybank.domain.usecase.transaction.StreamTransactions;
import com.example.mybank.infrastructure.driving.rest.dto.TransactionDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

import static com.example.mybank.infrastructure.driving.rest.security.JwtTokenService.extractClientId;

@RestController
@RequestMapping("/api/transactions")
@Profile("consumer")
public class TransactionController {

    private final ListTransactions listTransactions;
    private final StreamTransactions streamTransactions;

    public TransactionController(ListTransactions listTransactions, StreamTransactions streamTransactions) {
        this.listTransactions = listTransactions;
        this.streamTransactions = streamTransactions;
    }

    @GetMapping(params = "accountId")
    public List<TransactionDTO> listByAccount(@RequestParam String accountId) {
        Client.Id clientId = extractClientId();
        return listTransactions.forAccount(Account.Id.from(accountId), clientId).stream().map(TransactionDTO::fromDomain).toList();
    }

    @GetMapping(value = "/stream", params = "accountId", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<TransactionDTO> stream(@RequestParam String accountId) {
        Client.Id clientId = extractClientId();
        return streamTransactions.forAccount(Account.Id.from(accountId), clientId).map(TransactionDTO::fromDomain);
    }
}
