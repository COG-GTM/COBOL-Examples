package com.example.cobolmigration.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Application configuration and global exception handling.
 *
 * The {@link GlobalExceptionHandler} replaces the COBOL check-sql-state
 * paragraph (sql/sql_example.cbl lines 443-472) which checks SQLCODE /
 * SQLSTATE and terminates on error.  Instead of terminating, we return
 * appropriate HTTP error responses.
 */
@Configuration
public class AppConfig {

    @RestControllerAdvice
    public static class GlobalExceptionHandler {

        /**
         * Handles database access errors, replacing the COBOL pattern of
         * checking SQLCODE after every EXEC SQL statement.
         */
        @ExceptionHandler(DataAccessException.class)
        public ResponseEntity<ErrorResponse> handleDataAccessException(
                DataAccessException ex) {
            ErrorResponse error = new ErrorResponse(
                    "Database error",
                    ex.getMostSpecificCause().getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error);
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponse> handleIllegalArgument(
                IllegalArgumentException ex) {
            ErrorResponse error = new ErrorResponse(
                    "Invalid request", ex.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(error);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGenericException(
                Exception ex) {
            ErrorResponse error = new ErrorResponse(
                    "Internal server error", ex.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error);
        }
    }

    public record ErrorResponse(String error, String detail) {
    }
}
