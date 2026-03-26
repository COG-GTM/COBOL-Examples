package com.example.cobolmigration.repository;

import com.example.cobolmigration.model.Account;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * JPA repository for Account entities.
 * Replaces the COBOL cursor declarations in sql/sql_example.cbl.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /** Replaces ACCOUNT-ALL-CUR cursor: SELECT ... FROM ACCOUNTS ORDER BY ID. */
    List<Account> findAllByOrderByIdAsc();

    /**
     * Replaces ACCOUNT-DISABLED-CUR cursor:
     * SELECT ... FROM ACCOUNTS WHERE IS_ENABLED = 'N' ORDER BY ID.
     * Call with "N" to get disabled accounts.
     */
    List<Account> findByIsEnabledOrderByIdAsc(String isEnabled);

    /**
     * Replaces ACCOUNT-QUERY-CUR cursor:
     * SELECT ... FROM ACCOUNTS WHERE FIRST_NAME LIKE :term OR LAST_NAME LIKE :term
     * OR PHONE LIKE :term OR ADDRESS LIKE :term ORDER BY ID.
     */
    @Query("SELECT a FROM Account a WHERE a.firstName LIKE :term OR a.lastName LIKE :term"
            + " OR a.phone LIKE :term OR a.address LIKE :term ORDER BY a.id")
    List<Account> searchAccounts(@Param("term") String term);
}
