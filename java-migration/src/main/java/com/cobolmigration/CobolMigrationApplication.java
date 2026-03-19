package com.cobolmigration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application entry point.
 * Replaces the COBOL program-id entry points across all .cbl files.
 *
 * @see sql/sql_example.cbl - main-procedure
 */
@SpringBootApplication
public class CobolMigrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(CobolMigrationApplication.class, args);
    }
}
