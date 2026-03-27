package com.example.cobolmigration.repository;

import com.example.cobolmigration.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository replacing the embedded SQL cursors from sql/sql_example.cbl.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Replaces ACCOUNT-ALL-CUR cursor (sql/sql_example.cbl lines 119-124):
     * SELECT ID, FIRST_NAME, LAST_NAME, PHONE, ADDRESS, IS_ENABLED, CREATE_DT, MOD_DT
     * FROM ACCOUNTS ORDER BY ID
     */
    List<Account> findAllByOrderByIdAsc();

    /**
     * Replaces ACCOUNT-DISABLED-CUR cursor (sql/sql_example.cbl lines 130-137):
     * SELECT ... FROM ACCOUNTS WHERE IS_ENABLED = 'N' ORDER BY ID
     */
    List<Account> findByIsEnabledOrderByIdAsc(String isEnabled);

    /**
     * Replaces ACCOUNT-QUERY-CUR cursor (sql/sql_example.cbl lines 141-153):
     * SELECT ... FROM ACCOUNTS WHERE FIRST_NAME LIKE :search OR LAST_NAME LIKE :search
     * OR PHONE LIKE :search OR ADDRESS LIKE :search ORDER BY ID
     */
    @Query("SELECT a FROM Account a WHERE " +
           "a.firstName LIKE :searchTerm OR " +
           "a.lastName LIKE :searchTerm OR " +
           "a.phone LIKE :searchTerm OR " +
           "a.address LIKE :searchTerm " +
           "ORDER BY a.id ASC")
    List<Account> searchAccounts(@Param("searchTerm") String searchTerm);
}
