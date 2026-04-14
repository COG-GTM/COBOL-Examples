package com.cobolmigration.repository;

import com.cobolmigration.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Replaces the COBOL SQL cursors from sql/sql_example.cbl.
 * Spring Data JPA repository for Account entities.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    /**
     * Replaces ACCOUNT-ALL-CUR cursor (lines 118-125):
     * SELECT ID, FIRST_NAME, LAST_NAME, PHONE, ADDRESS, IS_ENABLED, CREATE_DT, MOD_DT
     * FROM ACCOUNTS ORDER BY ID
     */
    List<Account> findAllByOrderByIdAsc();

    /**
     * Replaces ACCOUNT-DISABLED-CUR cursor (lines 129-137):
     * SELECT ... FROM ACCOUNTS WHERE IS_ENABLED = 'N' ORDER BY ID
     */
    List<Account> findByIsEnabledOrderByIdAsc(String isEnabled);

    /**
     * Replaces ACCOUNT-QUERY-CUR cursor (lines 139-153):
     * SELECT ... FROM ACCOUNTS WHERE FIRST_NAME LIKE :search OR LAST_NAME LIKE :search
     * OR PHONE LIKE :search OR ADDRESS LIKE :search ORDER BY ID
     */
    @Query("SELECT a FROM Account a WHERE " +
           "a.firstName LIKE %:searchValue% OR " +
           "a.lastName LIKE %:searchValue% OR " +
           "a.phone LIKE %:searchValue% OR " +
           "a.address LIKE %:searchValue% " +
           "ORDER BY a.id ASC")
    List<Account> searchAccounts(@Param("searchValue") String searchValue);
}
