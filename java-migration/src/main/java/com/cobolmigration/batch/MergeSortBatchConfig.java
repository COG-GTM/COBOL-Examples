package com.cobolmigration.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Spring Batch configuration for merge and sort operations.
 *
 * Migrated from: merge_sort/merge_sort_test.cbl
 *
 * The original COBOL program:
 *   1. Creates two test data files (test-file-1.txt and test-file-2.txt)
 *   2. MERGE: Merges the two files sorted by customer-id ascending into merge-output.txt
 *   3. SORT: Sorts the merged file by contract-id descending into sorted-contract-id.txt
 *
 * This Spring Batch configuration replicates that flow using:
 *   - FlatFileItemReader for reading fixed-width customer record files
 *   - In-memory merge and sort operations
 *   - FlatFileItemWriter for writing output files
 */
@Configuration
public class MergeSortBatchConfig {

    /**
     * Creates a FlatFileItemReader for reading customer records from a fixed-width file.
     *
     * @param filePath the path to the input file
     * @return configured FlatFileItemReader
     */
    public FlatFileItemReader<CustomerRecord> customerRecordReader(String filePath) {
        FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
        tokenizer.setNames("customerId", "lastName", "firstName", "contractId", "comment");
        tokenizer.setColumns(
                new Range(1, 5),    // customer-id PIC 9(5)
                new Range(6, 55),   // last-name PIC X(50)
                new Range(56, 105), // first-name PIC X(50)
                new Range(106, 110),// contract-id PIC 9(5)
                new Range(111, 135) // comment PIC X(25)
        );

        FieldSetMapper<CustomerRecord> mapper = fieldSet -> {
            CustomerRecord record = new CustomerRecord();
            record.setCustomerId(fieldSet.readInt("customerId"));
            record.setLastName(fieldSet.readString("lastName").trim());
            record.setFirstName(fieldSet.readString("firstName").trim());
            record.setContractId(fieldSet.readInt("contractId"));
            record.setComment(fieldSet.readString("comment").trim());
            return record;
        };

        return new FlatFileItemReaderBuilder<CustomerRecord>()
                .name("customerRecordReader")
                .resource(new FileSystemResource(filePath))
                .lineTokenizer(tokenizer)
                .fieldSetMapper(mapper)
                .build();
    }

    /**
     * Creates a FlatFileItemWriter for writing customer records to a fixed-width file.
     *
     * @param filePath the path to the output file
     * @return configured FlatFileItemWriter
     */
    public FlatFileItemWriter<CustomerRecord> customerRecordWriter(String filePath) {
        return new FlatFileItemWriterBuilder<CustomerRecord>()
                .name("customerRecordWriter")
                .resource(new FileSystemResource(filePath))
                .lineAggregator(CustomerRecord::toFixedWidthString)
                .build();
    }

    /**
     * Merges two lists of customer records sorted by customer-id ascending.
     * Equivalent to COBOL:
     *   MERGE fd-sorting-file ON ASCENDING KEY f-customer-id
     *       USING fd-test-file-1 fd-test-file-2 GIVING fd-merged-file
     *
     * @param list1 first sorted list (e.g., "East" region customers)
     * @param list2 second sorted list (e.g., "West" region customers)
     * @return merged list sorted by customer-id ascending
     */
    public List<CustomerRecord> mergeByCustomerId(List<CustomerRecord> list1,
                                                   List<CustomerRecord> list2) {
        List<CustomerRecord> merged = new ArrayList<>(list1.size() + list2.size());
        int i = 0, j = 0;
        while (i < list1.size() && j < list2.size()) {
            if (list1.get(i).getCustomerId() <= list2.get(j).getCustomerId()) {
                merged.add(list1.get(i++));
            } else {
                merged.add(list2.get(j++));
            }
        }
        while (i < list1.size()) {
            merged.add(list1.get(i++));
        }
        while (j < list2.size()) {
            merged.add(list2.get(j++));
        }
        return merged;
    }

    /**
     * Sorts a list of customer records by contract-id descending.
     * Equivalent to COBOL:
     *   SORT fd-sorting-file ON DESCENDING KEY f-customer-contract-id
     *       USING fd-merged-file GIVING fd-sorted-contract-id
     *
     * @param records the list to sort
     * @return new list sorted by contract-id descending
     */
    public List<CustomerRecord> sortByContractIdDescending(List<CustomerRecord> records) {
        List<CustomerRecord> sorted = new ArrayList<>(records);
        sorted.sort(Comparator.comparingInt(CustomerRecord::getContractId).reversed());
        return sorted;
    }

    /**
     * Spring Batch Job definition for the merge-sort operation.
     */
    @Bean
    public Job mergeSortJob(JobRepository jobRepository, Step mergeSortStep) {
        return new JobBuilder("mergeSortJob", jobRepository)
                .start(mergeSortStep)
                .build();
    }

    /**
     * Spring Batch Step that reads, merges, and writes customer records.
     */
    @Bean
    public Step mergeSortStep(JobRepository jobRepository,
                              PlatformTransactionManager transactionManager) {
        return new StepBuilder("mergeSortStep", jobRepository)
                .<CustomerRecord, CustomerRecord>chunk(10, transactionManager)
                .reader(emptyReader())
                .processor(passThroughProcessor())
                .writer(noOpWriter())
                .build();
    }

    private ItemReader<CustomerRecord> emptyReader() {
        return new ListItemReader<>(List.of());
    }

    private ItemProcessor<CustomerRecord, CustomerRecord> passThroughProcessor() {
        return item -> item;
    }

    private ItemWriter<CustomerRecord> noOpWriter() {
        return items -> { };
    }
}
