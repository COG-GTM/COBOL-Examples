package com.example.cobolmigration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class.
 * Migrated from the collection of GnuCOBOL example programs in COG-GTM/COBOL-Examples.
 * Starts a Spring Shell CLI that exposes commands equivalent to the original COBOL programs.
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
