package com.example.cobolmigration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Spring Boot port of the GnuCOBOL {@code sql/sql_example.cbl}
 * program. The original COBOL {@code PROCEDURE DIVISION} opened an ODBC connection
 * and drove a console menu; here Spring Boot bootstraps an embedded web server and
 * a JPA-managed datasource instead.
 */
@SpringBootApplication
public class CobolMigrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(CobolMigrationApplication.class, args);
    }
}
