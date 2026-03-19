package com.cobolmigration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the COBOL-to-Spring Boot migration.
 *
 * <p>This application replaces the terminal-based COBOL programs with a
 * REST API backed by Spring Boot and JPA. The original COBOL source files
 * remain in the repository for reference.</p>
 */
@SpringBootApplication
public class CobolMigrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(CobolMigrationApplication.class, args);
    }
}
