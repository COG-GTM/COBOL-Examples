package com.cobolmigration.repository;

import com.cobolmigration.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Account entity.
 * Replaces COBOL cursor-based data access:
 *   - DECLARE ACCOUNT-ALL-CUR CURSOR (findAll)
 *   - DECLARE ACCOUNT-DISABLED-CUR CURSOR (findByIsEnabled)
 *   - DECLARE ACCOUNT-QUERY-CUR CURSOR with LIKE (searchAccounts)
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Find all accounts ordered by id.
     * Replaces: DECLARE ACCOUNT-ALL-CUR CURSOR FOR SELECT ... FROM ACCOUNTS ORDER BY ID
     */
    List<Account> findAllByOrderByIdAsc();

    /**
     * Find accounts by enabled status.
     * Replaces: DECLARE ACCOUNT-DISABLED-CUR CURSOR ... WHERE IS_ENABLED = 'N'
     */
    List<Account> findByIsEnabledOrderByIdAsc(String isEnabled);

    /**
     * Search accounts across multiple columns using LIKE.
     * Replaces the COBOL query-accounts paragraph which uses:
     *   WHERE FIRST_NAME LIKE :search OR LAST_NAME LIKE :search
     *   OR PHONE LIKE :search OR ADDRESS LIKE :search
     */
    @Query("SELECT a FROM Account a WHERE " +
           "LOWER(a.firstName) LIKE LOWER(CONCAT('%', :searchValue, '%')) OR " +
           "LOWER(a.lastName) LIKE LOWER(CONCAT('%', :searchValue, '%')) OR " +
           "LOWER(a.phone) LIKE LOWER(CONCAT('%', :searchValue, '%')) OR " +
           "LOWER(a.address) LIKE LOWER(CONCAT('%', :searchValue, '%')) " +
           "ORDER BY a.id ASC")
    List<Account> searchAccounts(@Param("searchValue") String searchValue);
}
