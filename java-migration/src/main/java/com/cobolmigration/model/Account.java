package com.cobolmigration.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * JPA entity mapping to the ACCOUNTS table.
 *
 * Migrated from: sql/sql_example.cbl (lines 44-52)
 *
 * Original COBOL record structure:
 *   01  ws-sql-account-record.
 *       05  ws-sql-account-id           PIC 9(5).
 *       05  ws-sql-account-first-name   PIC X(8).
 *       05  ws-sql-account-last-name    PIC X(8).
 *       05  ws-sql-account-phone        PIC X(10).
 *       05  ws-sql-account-address      PIC X(22).
 *       05  ws-sql-account-is-enabled   PIC X.
 *       05  ws-sql-account-create-dt    PIC X(20).
 *       05  ws-sql-account-mod-dt       PIC X(20).
 */
@Entity
@Table(name = "ACCOUNTS")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "FIRST_NAME", nullable = false)
    private String firstName;

    @Column(name = "LAST_NAME", nullable = false)
    private String lastName;

    @Column(name = "PHONE", nullable = false)
    private String phone;

    @Column(name = "ADDRESS", nullable = false)
    private String address;

    @Column(name = "IS_ENABLED", nullable = false, length = 1)
    private String isEnabled;

    @Column(name = "CREATE_DT")
    private LocalDateTime createDt;

    @Column(name = "MOD_DT")
    private LocalDateTime modDt;

    public Account() {
    }

    public Account(String firstName, String lastName, String phone, String address,
                   String isEnabled, LocalDateTime createDt, LocalDateTime modDt) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
        this.isEnabled = isEnabled;
        this.createDt = createDt;
        this.modDt = modDt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    @Override
    public String toString() {
        return String.format("%-5d | %-8s | %-8s | %-10s | %-22s | %s",
                id != null ? id : 0, firstName, lastName, phone, address, isEnabled);
    }
}
