package com.cobol.examples.core.sql;

/** Account row, ported from {@code ws-account-record} in {@code sql/sql_example.cbl}. */
public record Account(
        int id,
        String firstName,
        String lastName,
        String phone,
        String address,
        boolean enabled) {
}
