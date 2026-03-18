package com.migration.database;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Phase 5: Database Layer - Account Entity
 *
 * Maps to the COBOL ws-sql-account-record from sql/sql_example.cbl
 * and the PostgreSQL ACCOUNTS table from sql/create_test_db.sql.
 *
 * COBOL record:
 *   01 ws-sql-account-record.
 *       05 ws-sql-account-id             PIC 9(5).
 *       05 ws-sql-account-first-name     PIC X(8).
 *       05 ws-sql-account-last-name      PIC X(8).
 *       05 ws-sql-account-phone          PIC X(10).
 *       05 ws-sql-account-address        PIC X(22).
 *       05 ws-sql-account-is-enabled     PIC X.
 *       05 ws-sql-account-create-dt      PIC X(20).
 *       05 ws-sql-account-mod-dt         PIC X(20).
 *
 * Note: COBOL PIC X fields are fixed-width and space-padded.
 * The @PrePersist/@PreUpdate callback trims all string fields to handle
 * the fixed-width padding issue documented in sql/README.md.
 */
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "is_enabled", nullable = false, length = 1)
    private String isEnabled;

    @Column(name = "create_dt")
    private LocalDateTime createDt;

    @Column(name = "mod_dt")
    private LocalDateTime modDt;

    public Account() {
    }

    public Account(String firstName, String lastName, String phone,
                   String address, String isEnabled) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
        this.isEnabled = isEnabled;
    }

    /**
     * Handles the fixed-width string padding issue from COBOL.
     * COBOL PIC X fields pad with spaces to the declared width.
     * This callback trims all string fields before persisting to avoid
     * storing padded values in the database.
     */
    @PrePersist
    @PreUpdate
    public void trimFields() {
        if (firstName != null) firstName = firstName.strip();
        if (lastName != null) lastName = lastName.strip();
        if (phone != null) phone = phone.strip();
        if (address != null) address = address.strip();
        if (isEnabled != null) isEnabled = isEnabled.strip();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(String isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean isEnabled() {
        return "Y".equalsIgnoreCase(isEnabled);
    }

    public LocalDateTime getCreateDt() {
        return createDt;
    }

    public void setCreateDt(LocalDateTime createDt) {
        this.createDt = createDt;
    }

    public LocalDateTime getModDt() {
        return modDt;
    }

    public void setModDt(LocalDateTime modDt) {
        this.modDt = modDt;
    }
}
