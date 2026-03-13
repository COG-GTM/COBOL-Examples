package com.cobolmigration.repository;

import com.cobolmigration.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for Account entities.
 *
 * Migrated from: sql/sql_example.cbl (lines 118-155)
 *
 * Replaces three COBOL SQL cursors:
 *   - ACCOUNT-ALL-CUR: SELECT * FROM ACCOUNTS ORDER BY ID
 *   - ACCOUNT-DISABLED-CUR: SELECT * FROM ACCOUNTS WHERE IS_ENABLED = 'N' ORDER BY ID
 *   - ACCOUNT-QUERY-CUR: SELECT * FROM ACCOUNTS WHERE FIRST_NAME LIKE :val
 *                         OR LAST_NAME LIKE :val OR PHONE LIKE :val OR ADDRESS LIKE :val ORDER BY ID
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Replaces ACCOUNT-ALL-CUR cursor.
     * Retrieves all accounts ordered by ID ascending.
     */
    List<Account> findAllByOrderByIdAsc();

    /**
     * Replaces ACCOUNT-DISABLED-CUR cursor.
     * Retrieves accounts filtered by isEnabled flag, ordered by ID ascending.
     *
     * Usage: findByIsEnabledOrderByIdAsc("N") for disabled accounts.
     */
    List<Account> findByIsEnabledOrderByIdAsc(String isEnabled);

    /**
     * Replaces ACCOUNT-QUERY-CUR cursor.
     * Searches across firstName, lastName, phone, and address using LIKE.
     *
     * In the original COBOL, the search value is wrapped with '%' wildcards
     * and trimmed before being passed to the SQL query. The caller should
     * pass the search term already wrapped with '%' characters.
     */
    @Query("SELECT a FROM Account a WHERE " +
           "a.firstName LIKE :searchValue " +
           "OR a.lastName LIKE :searchValue " +
           "OR a.phone LIKE :searchValue " +
           "OR a.address LIKE :searchValue " +
           "ORDER BY a.id ASC")
    List<Account> searchAccounts(@Param("searchValue") String searchValue);
}
