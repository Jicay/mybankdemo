package com.example.mybank.infrastructure.driving.batch.processor;

import com.example.mybank.domain.model.Account;
import com.example.mybank.domain.model.Amount;
import com.example.mybank.domain.model.Transaction;
import com.example.mybank.infrastructure.driving.batch.dto.TransactionImportDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
@Profile("consumer")
public class TransactionImportProcessor implements ItemProcessor<TransactionImportDto, Transaction> {

    private static final Logger logger = LoggerFactory.getLogger(TransactionImportProcessor.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Returning null filters the line out: it is counted as filtered, not written
    @Override
    public Transaction process(TransactionImportDto item) {
        if (item.accountId() == null || item.accountId().isBlank()) {
            logger.warn("Transaction ignorée: accountId manquant");
            return null;
        }
        if (item.amountCents() == null) {
            logger.warn("Transaction ignorée: montant manquant");
            return null;
        }
        if (item.type() == null || item.type().isBlank()) {
            logger.warn("Transaction ignorée: type manquant");
            return null;
        }
        if (item.date() == null || item.date().isBlank()) {
            logger.warn("Transaction ignorée: date manquante");
            return null;
        }

        Transaction.Type type;
        try {
            type = Transaction.Type.valueOf(item.type().toUpperCase());
        } catch (IllegalArgumentException _) {
            logger.warn("Transaction ignorée: type invalide '{}'", item.type());
            return null;
        }

        LocalDateTime localDateTime;
        try {
            localDateTime = LocalDateTime.parse(item.date(), DATE_FORMATTER);
        } catch (DateTimeParseException _) {
            logger.warn("Transaction ignorée: format de date invalide '{}'", item.date());
            return null;
        }

        return new Transaction(
                new Transaction.Id(),
                Account.Id.from(item.accountId()),
                Amount.fromCents(item.amountCents()),
                type,
                localDateTime.atZone(ZoneId.systemDefault()).toInstant()
        );
    }
}
