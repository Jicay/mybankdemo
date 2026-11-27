package com.example.mybank.infrastructure.driving.batch.writer;

import com.example.mybank.domain.model.Transaction;
import com.example.mybank.domain.usecase.transaction.CreateTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("consumer")
public class TransactionImportWriter implements ItemWriter<Transaction> {

    private static final Logger logger = LoggerFactory.getLogger(TransactionImportWriter.class);
    private final CreateTransaction createTransaction;

    public TransactionImportWriter(CreateTransaction createTransaction) {
        this.createTransaction = createTransaction;
    }

    @Override
    public void write(Chunk<? extends Transaction> chunk) {
        for (Transaction transaction : chunk) {
            createTransaction.create(transaction.accountId(), transaction.amount(), transaction.type(), transaction.date());
        }
        logger.info("Batch de {} transactions importées avec succès", chunk.size());
    }
}
