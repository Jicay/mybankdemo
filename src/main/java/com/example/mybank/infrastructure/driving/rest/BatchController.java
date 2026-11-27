package com.example.mybank.infrastructure.driving.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/batch")
@Profile("consumer")
public class BatchController {

    private static final Logger logger = LoggerFactory.getLogger(BatchController.class);

    private final JobOperator jobOperator;
    private final Job transactionImportJob;
    private final JobRepository jobRepository;

    public BatchController(JobOperator jobOperator, Job transactionImportJob, JobRepository jobRepository) {
        this.jobOperator = jobOperator;
        this.transactionImportJob = transactionImportJob;
        this.jobRepository = jobRepository;
    }

    @PostMapping("/import-transactions")
    public ResponseEntity<Map<String, Object>> importTransactions() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("startTime", LocalDateTime.now().toString())
                    .toJobParameters();

            JobExecution jobExecution = jobOperator.start(transactionImportJob, jobParameters);

            logger.info("Job d'importation de transactions démarré avec l'ID: {}", jobExecution.getId());

            return ResponseEntity.ok(Map.of(
                    "message", "Job d'importation démarré avec succès",
                    "jobId", jobExecution.getId(),
                    "status", jobExecution.getStatus().name(),
                    "startTime", jobExecution.getStartTime()
            ));
        } catch (Exception e) {
            logger.error("Erreur lors du démarrage du job d'importation: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Erreur lors du démarrage du job: " + e.getMessage()));
        }
    }

    @GetMapping("/job-status/{jobId}")
    public ResponseEntity<Map<String, Object>> getJobStatus(@PathVariable long jobId) {
        JobExecution jobExecution = jobRepository.getJobExecution(jobId);
        if (jobExecution == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(Map.of(
                "jobId", jobExecution.getId(),
                "status", jobExecution.getStatus().name(),
                "startTime", jobExecution.getStartTime(),
                "endTime", jobExecution.getEndTime(),
                "exitStatus", jobExecution.getExitStatus().getExitCode(),
                "stepExecutions", jobExecution.getStepExecutions().stream()
                        .map(step -> Map.of(
                                "stepName", step.getStepName(),
                                "status", step.getStatus().name(),
                                "readCount", step.getReadCount(),
                                "writeCount", step.getWriteCount(),
                                "filterCount", step.getFilterCount(),
                                "skipCount", step.getSkipCount()
                        ))
                        .toList()
        ));
    }

    @GetMapping("/jobs")
    public ResponseEntity<Map<String, Object>> listJobs() {
        var jobInstances = jobRepository.getJobNames().stream()
                .flatMap(jobName -> jobRepository.getJobInstances(jobName, 0, 10).stream())
                .map(instance -> Map.of(
                        "jobName", instance.getJobName(),
                        "instanceId", instance.getInstanceId(),
                        "executions", jobRepository.getJobExecutions(instance).stream()
                                .map(execution -> Map.of(
                                        "executionId", execution.getId(),
                                        "status", execution.getStatus().name(),
                                        "startTime", execution.getStartTime(),
                                        "endTime", execution.getEndTime()
                                ))
                                .toList()
                ))
                .toList();

        return ResponseEntity.ok(Map.of(
                "totalJobs", jobInstances.size(),
                "jobs", jobInstances
        ));
    }
}
