package com.migration.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.nio.file.Path;

/**
 * Phase 4: Spring Batch job configuration for merge/sort processing.
 *
 * Provides a Spring Batch alternative to the FileMergeService approach.
 * The COBOL MERGE/SORT operations are modeled as batch job steps:
 *
 * Step 1: Merge two input files sorted by customer ID
 * Step 2: Re-sort the merged file by contract ID descending
 *
 * This mirrors the COBOL program flow:
 *   perform merge-and-display-files
 *   perform sort-and-display-file
 */
@Configuration
public class MergeSortJobConfig {

    private final FileMergeService fileMergeService;

    public MergeSortJobConfig(FileMergeService fileMergeService) {
        this.fileMergeService = fileMergeService;
    }

    @Bean
    public Job mergeSortJob(JobRepository jobRepository, Step mergeStep, Step sortStep) {
        return new JobBuilder("mergeSortJob", jobRepository)
                .start(mergeStep)
                .next(sortStep)
                .build();
    }

    @Bean
    public Step mergeStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        Tasklet mergeTasklet = (contribution, chunkContext) -> {
            Path file1 = Path.of("test-file-1.txt");
            Path file2 = Path.of("test-file-2.txt");
            Path output = Path.of("merge-output.txt");
            fileMergeService.mergeFilesToOutput(file1, file2, output);
            return RepeatStatus.FINISHED;
        };

        return new StepBuilder("mergeStep", jobRepository)
                .tasklet(mergeTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step sortStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        Tasklet sortTasklet = (contribution, chunkContext) -> {
            Path input = Path.of("merge-output.txt");
            Path output = Path.of("sorted-contract-id.txt");
            fileMergeService.sortToOutput(input, output);
            return RepeatStatus.FINISHED;
        };

        return new StepBuilder("sortStep", jobRepository)
                .tasklet(sortTasklet, transactionManager)
                .build();
    }
}
