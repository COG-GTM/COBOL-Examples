package com.example.migration.repository;

import com.example.migration.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Spring Data JPA repository replacing the three COBOL SQL cursors
 * from sql/sql_example.cbl (lines 118-153).
 *
 * Cursor mapping:
 *   ACCOUNT-ALL-CUR      -> findAllByOrderByIdAsc()
 *   ACCOUNT-DISABLED-CUR -> findByIsEnabledOrderByIdAsc("N")
 *   ACCOUNT-QUERY-CUR    -> searchAccounts(searchValue)
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Replaces ACCOUNT-ALL-CUR: SELECT * FROM ACCOUNTS ORDER BY ID
     * (sql_example.cbl lines 119-125)
     */
    List<Account> findAllByOrderByIdAsc();

    /**
     * Replaces ACCOUNT-DISABLED-CUR: SELECT * FROM ACCOUNTS WHERE IS_ENABLED = 'N' ORDER BY ID
     * (sql_example.cbl lines 130-137)
     */
    List<Account> findByIsEnabledOrderByIdAsc(String isEnabled);

    /**
     * Replaces ACCOUNT-QUERY-CUR: searches across first_name, last_name, phone, address
     * using LIKE with wildcards (sql_example.cbl lines 142-153).
     *
     * The COBOL version wraps the search value with '%' wildcards and uses LIKE.
     * This query replicates that behavior.
     */
    @Query("SELECT a FROM Account a WHERE " +
           "a.firstName LIKE :searchValue " +
           "OR a.lastName LIKE :searchValue " +
           "OR a.phone LIKE :searchValue " +
           "OR a.address LIKE :searchValue " +
           "ORDER BY a.id ASC")
    List<Account> searchAccounts(@Param("searchValue") String searchValue);
}
