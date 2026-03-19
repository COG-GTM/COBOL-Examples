package com.cobolmigration.repository;

import com.cobolmigration.model.Account;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository replacing the three COBOL cursors defined in
 * sql/sql_example.cbl (lines 118-153).
 *
 * <p>Cursor mapping:
 * <ul>
 *   <li>ACCOUNT-ALL-CUR &rarr; {@link #findAllByOrderByIdAsc()}</li>
 *   <li>ACCOUNT-DISABLED-CUR &rarr; {@link #findByIsEnabledOrderByIdAsc(Character)}</li>
 *   <li>ACCOUNT-QUERY-CUR &rarr; {@link #searchAccounts(String)}</li>
 * </ul>
 *
 * @see sql/sql_example.cbl
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Replaces ACCOUNT-ALL-CUR: SELECT ... FROM ACCOUNTS ORDER BY ID.
     */
    List<Account> findAllByOrderByIdAsc();

    /**
     * Replaces ACCOUNT-DISABLED-CUR: SELECT ... FROM ACCOUNTS WHERE IS_ENABLED = ? ORDER BY ID.
     */
    List<Account> findByIsEnabledOrderByIdAsc(Character isEnabled);

    /**
     * Replaces ACCOUNT-QUERY-CUR: searches across firstName, lastName, phone, and address
     * using LIKE with wildcards. The COBOL version wraps the search term with '%' wildcards
     * (sql_example.cbl lines 333-337).
     *
     * @param searchTerm the search term (wildcards should be included by the caller)
     * @return matching accounts ordered by ID
     */
    @Query("SELECT a FROM Account a WHERE " +
           "a.firstName LIKE :searchTerm OR " +
           "a.lastName LIKE :searchTerm OR " +
           "a.phone LIKE :searchTerm OR " +
           "a.address LIKE :searchTerm " +
           "ORDER BY a.id ASC")
    List<Account> searchAccounts(@Param("searchTerm") String searchTerm);
}
