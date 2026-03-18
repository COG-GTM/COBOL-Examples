package com.migration.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Phase 5: Database Layer - Account Repository
 *
 * Spring Data JPA repository replacing COBOL embedded SQL cursor declarations.
 *
 * COBOL cursor mappings:
 *
 * ACCOUNT-ALL-CUR: SELECT ... FROM ACCOUNTS ORDER BY ID
 *   -> findAllByOrderByIdAsc()
 *
 * ACCOUNT-DISABLED-CUR: SELECT ... FROM ACCOUNTS WHERE IS_ENABLED = 'N' ORDER BY ID
 *   -> findByIsEnabledOrderByIdAsc("N")
 *
 * ACCOUNT-QUERY-CUR: SELECT ... FROM ACCOUNTS WHERE FIRST_NAME LIKE :value
 *                     OR LAST_NAME LIKE :value OR PHONE LIKE :value
 *                     OR ADDRESS LIKE :value ORDER BY ID
 *   -> searchAccounts(value)
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    /**
     * Replaces COBOL ACCOUNT-ALL-CUR cursor.
     * COBOL: SELECT ID, FIRST_NAME, ... FROM ACCOUNTS ORDER BY ID
     */
    List<Account> findAllByOrderByIdAsc();

    /**
     * Replaces COBOL ACCOUNT-DISABLED-CUR cursor.
     * COBOL: SELECT ... FROM ACCOUNTS WHERE IS_ENABLED = 'N' ORDER BY ID
     */
    List<Account> findByIsEnabledOrderByIdAsc(String isEnabled);

    /**
     * Replaces COBOL ACCOUNT-QUERY-CUR cursor.
     * COBOL: SELECT ... FROM ACCOUNTS WHERE FIRST_NAME LIKE :value
     *        OR LAST_NAME LIKE :value OR PHONE LIKE :value
     *        OR ADDRESS LIKE :value ORDER BY ID
     *
     * Note: In COBOL, the search value required explicit length handling
     * to avoid matching blank-padded spaces. In JPA, String parameters
     * are handled correctly without manual length management.
     */
    @Query("SELECT a FROM Account a WHERE " +
           "a.firstName LIKE :searchValue OR " +
           "a.lastName LIKE :searchValue OR " +
           "a.phone LIKE :searchValue OR " +
           "a.address LIKE :searchValue " +
           "ORDER BY a.id ASC")
    List<Account> searchAccounts(@Param("searchValue") String searchValue);
}
