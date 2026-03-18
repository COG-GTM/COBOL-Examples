package com.migration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot main application class.
 *
 * This application migrates COBOL example programs from the COBOL-Examples
 * repository into Java/Spring Boot equivalents, organized by phase:
 *
 * Phase 1: String Utilities (com.migration.stringutils)
 * Phase 2: Data Structures (com.migration.datastructures)
 * Phase 3: Serialization (com.migration.serialization)
 * Phase 4: File Processing / Batch (com.migration.batch)
 * Phase 5: Database Layer (com.migration.database)
 * Phase 6: Subprogram Architecture / Service Layer (com.migration.services)
 * Phase 7: UI Layer / REST Controllers (com.migration.web)
 * Phase 8: Report Generation (com.migration.reports)
 */
@SpringBootApplication
public class MigrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(MigrationApplication.class, args);
    }
}
