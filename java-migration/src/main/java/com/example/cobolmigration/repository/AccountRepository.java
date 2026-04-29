package com.example.cobolmigration.repository;

import com.example.cobolmigration.entity.Account;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository replacing the three COBOL SQL cursors
 * defined in sql/sql_example.cbl lines 118-160.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    /**
     * Replaces ACCOUNT-ALL-CUR: SELECT ... FROM ACCOUNTS ORDER BY ID.
     */
    List<Account> findAllByOrderByIdAsc();

    /**
     * Replaces ACCOUNT-DISABLED-CUR: SELECT ... WHERE IS_ENABLED = :isEnabled ORDER BY ID.
     * Pass "N" to retrieve disabled accounts.
     */
    List<Account> findByIsEnabledOrderByIdAsc(String isEnabled);

    /**
     * Replaces ACCOUNT-QUERY-CUR which searches across FIRST_NAME, LAST_NAME,
     * PHONE, and ADDRESS columns using LIKE '%value%'.
     *
     * The COBOL implementation (lines 318-394) trims the search input and wraps
     * it with '%' wildcards via variable-length string handling (lines 54-67).
     * In Java the caller should .trim() the input; the repository uses
     * LIKE with concatenated wildcards to produce the same behaviour.
     */
    @Query("SELECT a FROM Account a WHERE "
         + "a.firstName LIKE %:searchValue% OR "
         + "a.lastName  LIKE %:searchValue% OR "
         + "a.phone     LIKE %:searchValue% OR "
         + "a.address   LIKE %:searchValue% "
         + "ORDER BY a.id ASC")
    List<Account> searchAccounts(@Param("searchValue") String searchValue);
}
