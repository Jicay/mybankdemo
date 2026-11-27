package com.example.mybank.infrastructure.driving.batch.config;

import com.example.mybank.domain.model.Transaction;
import com.example.mybank.infrastructure.driving.batch.dto.TransactionImportDto;
import com.example.mybank.infrastructure.driving.batch.processor.TransactionImportProcessor;
import com.example.mybank.infrastructure.driving.batch.writer.TransactionImportWriter;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Profile("consumer")
public class TransactionImportJobConfig {

    @Bean
    public FlatFileItemReader<TransactionImportDto> transactionReader() {
        return new FlatFileItemReaderBuilder<TransactionImportDto>()
                .name("transactionItemReader")
                .resource(new ClassPathResource("import/transactions.csv"))
                .delimited()
                .names("accountId", "amountCents", "type", "date")
                .linesToSkip(1)
                .targetType(TransactionImportDto.class)
                .build();
    }

    @Bean
    public Step transactionImportStep(JobRepository jobRepository,
                                      PlatformTransactionManager transactionManager,
                                      FlatFileItemReader<TransactionImportDto> reader,
                                      TransactionImportProcessor processor,
                                      TransactionImportWriter writer) {
        return new StepBuilder("transactionImportStep", jobRepository)
                .<TransactionImportDto, Transaction>chunk(100)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job transactionImportJob(JobRepository jobRepository, Step transactionImportStep) {
        return new JobBuilder("transactionImportJob", jobRepository)
                .start(transactionImportStep)
                .build();
    }
}
