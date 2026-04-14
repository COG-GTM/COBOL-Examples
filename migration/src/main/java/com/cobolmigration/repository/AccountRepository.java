package com.cobolmigration.repository;

import com.cobolmigration.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA repository for Account entity, replacing COBOL cursor declarations
 * from sql/sql_example.cbl (lines 119-153).
 *
 * COBOL cursor mappings:
 *   ACCOUNT-ALL-CUR      -> findAllByOrderByIdAsc()
 *   ACCOUNT-DISABLED-CUR -> findByEnabledFalseOrderByIdAsc()
 *   ACCOUNT-QUERY-CUR    -> searchByTerm()
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Replaces ACCOUNT-ALL-CUR:
     *   SELECT ID, FIRST_NAME, LAST_NAME, PHONE, ADDRESS, IS_ENABLED, CREATE_DT, MOD_DT
     *   FROM ACCOUNTS ORDER BY ID
     */
    List<Account> findAllByOrderByIdAsc();

    /**
     * Replaces ACCOUNT-DISABLED-CUR:
     *   SELECT ... FROM ACCOUNTS WHERE IS_ENABLED = 'N' ORDER BY ID
     */
    List<Account> findByEnabledFalseOrderByIdAsc();

    /**
     * Replaces ACCOUNT-QUERY-CUR:
     *   SELECT ... FROM ACCOUNTS
     *   WHERE FIRST_NAME LIKE :term
     *     OR LAST_NAME LIKE :term
     *     OR PHONE LIKE :term
     *     OR ADDRESS LIKE :term
     *   ORDER BY ID
     *
     * In the COBOL program (query-accounts paragraph), the search term
     * is wrapped with '%' wildcards and trimmed before being passed to the query.
     */
    @Query("SELECT a FROM Account a WHERE " +
           "a.firstName LIKE %:term% OR " +
           "a.lastName LIKE %:term% OR " +
           "a.phone LIKE %:term% OR " +
           "a.address LIKE %:term% " +
           "ORDER BY a.id")
    List<Account> searchByTerm(@Param("term") String term);
}
