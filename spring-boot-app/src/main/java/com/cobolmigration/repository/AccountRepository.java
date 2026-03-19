package com.cobolmigration.repository;

import com.cobolmigration.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link Account} entity.
 *
 * <p>Replaces the SQL cursors declared in {@code sql/sql_example.cbl}
 * (lines 118-153).</p>
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    /**
     * Retrieves all accounts ordered by ID ascending.
     *
     * <p>Replaces ACCOUNT-ALL-CUR:
     * <pre>
     *   DECLARE ACCOUNT-ALL-CUR CURSOR FOR
     *   SELECT ID, FIRST_NAME, LAST_NAME, PHONE,
     *          ADDRESS, IS_ENABLED, CREATE_DT, MOD_DT
     *   FROM ACCOUNTS ORDER BY ID;
     * </pre>
     */
    List<Account> findAllByOrderByIdAsc();

    /**
     * Retrieves all disabled accounts ordered by ID ascending.
     *
     * <p>Replaces ACCOUNT-DISABLED-CUR:
     * <pre>
     *   DECLARE ACCOUNT-DISABLED-CUR CURSOR FOR
     *   SELECT ... FROM ACCOUNTS WHERE IS_ENABLED = 'N' ORDER BY ID;
     * </pre>
     */
    List<Account> findByEnabledFalseOrderByIdAsc();

    /**
     * Searches accounts by matching the search value against first name,
     * last name, phone, or address using LIKE.
     *
     * <p>Replaces ACCOUNT-QUERY-CUR:
     * <pre>
     *   DECLARE ACCOUNT-QUERY-CUR CURSOR FOR
     *   SELECT ... FROM ACCOUNTS
     *   WHERE FIRST_NAME LIKE :ws-search-value
     *      OR LAST_NAME LIKE :ws-search-value
     *      OR PHONE LIKE :ws-search-value
     *      OR ADDRESS LIKE :ws-search-value
     *   ORDER BY ID;
     * </pre>
     */
    @Query("SELECT a FROM Account a WHERE a.firstName LIKE :searchValue "
            + "OR a.lastName LIKE :searchValue "
            + "OR a.phone LIKE :searchValue "
            + "OR a.address LIKE :searchValue "
            + "ORDER BY a.id")
    List<Account> searchAccounts(@Param("searchValue") String searchValue);
}
