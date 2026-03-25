package com.cobolmigration.repository;

import com.cobolmigration.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Account entity.
 * Replaces the COBOL CURSOR declarations in sql/sql_example.cbl (lines 118-153).
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Retrieves all accounts ordered by ID ascending.
     * Replaces DECLARE ACCOUNT-ALL-CUR CURSOR in sql_example.cbl (lines 119-125).
     */
    List<Account> findAllByOrderByIdAsc();

    /**
     * Retrieves accounts filtered by enabled status.
     * Replaces DECLARE ACCOUNT-DISABLED-CUR CURSOR in sql_example.cbl (lines 130-137).
     */
    List<Account> findByIsEnabled(String isEnabled);

    /**
     * Searches accounts by term across first_name, last_name, phone, and address fields.
     * Replaces the query-accounts paragraph using ws-search-value in sql_example.cbl (lines 65-67, 141-153).
     */
    @Query("SELECT a FROM Account a WHERE " +
           "LOWER(a.firstName) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "LOWER(a.lastName) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "LOWER(a.phone) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "LOWER(a.address) LIKE LOWER(CONCAT('%', :term, '%')) " +
           "ORDER BY a.id ASC")
    List<Account> searchAccounts(@Param("term") String term);
}
