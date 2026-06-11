package com.example.cobolmigration.repository;

import com.example.cobolmigration.model.Account;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA replacement for the COBOL {@code EXEC SQL DECLARE CURSOR}
 * statements in {@code sql/sql_example.cbl} (lines 118-153). Each cursor becomes
 * a query method; Spring Data manages the open/fetch/close lifecycle that the
 * COBOL paragraphs handled manually.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Replaces {@code ACCOUNT-ALL-CUR} (SELECT ... FROM ACCOUNTS ORDER BY ID).
     * Provided by {@link JpaRepository#findAll()}; declared here for an explicit
     * ordered variant matching the original cursor.
     */
    List<Account> findAllByOrderByIdAsc();

    /**
     * Replaces {@code ACCOUNT-DISABLED-CUR} (WHERE IS_ENABLED = 'N' ORDER BY ID).
     * The boolean is persisted as 'Y'/'N' via {@code YesNoBooleanConverter}.
     */
    List<Account> findByIsEnabledFalseOrderByIdAsc();

    /**
     * Replaces {@code ACCOUNT-QUERY-CUR} which matched a LIKE term against
     * FIRST_NAME, LAST_NAME, PHONE or ADDRESS. The caller supplies the search
     * term; this method wraps it with SQL wildcards (the COBOL added '%' manually
     * in the {@code query-accounts} paragraph, lines 333-337).
     */
    @Query("SELECT a FROM Account a WHERE "
            + "LOWER(a.firstName) LIKE LOWER(:term) "
            + "OR LOWER(a.lastName) LIKE LOWER(:term) "
            + "OR LOWER(a.phone) LIKE LOWER(:term) "
            + "OR LOWER(a.address) LIKE LOWER(:term) "
            + "ORDER BY a.id ASC")
    List<Account> search(@Param("term") String term);
}
