package com.cobolmigration.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * JPA entity mapping from the COBOL ws-sql-account-record in sql/sql_example.cbl.
 *
 * <p>COBOL field mapping:
 * <ul>
 *   <li>ws-sql-account-id (PIC 9(5)) &rarr; id (Long, auto-generated)</li>
 *   <li>ws-sql-account-first-name (PIC X(8)) &rarr; firstName (String)</li>
 *   <li>ws-sql-account-last-name (PIC X(8)) &rarr; lastName (String)</li>
 *   <li>ws-sql-account-phone (PIC X(10)) &rarr; phone (String)</li>
 *   <li>ws-sql-account-address (PIC X(22)) &rarr; address (String)</li>
 *   <li>ws-sql-account-is-enabled (PIC X) &rarr; isEnabled (Character, 'Y'/'N')</li>
 *   <li>ws-sql-account-create-dt (PIC X(20)) &rarr; createDt (LocalDateTime)</li>
 *   <li>ws-sql-account-mod-dt (PIC X(20)) &rarr; modDt (LocalDateTime)</li>
 * </ul>
 *
 * @see sql/sql_example.cbl lines 44-52
 * @see sql/create_test_db.sql
 */
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "is_enabled", nullable = false, length = 1)
    private Character isEnabled;

    @Column(name = "create_dt")
    private LocalDateTime createDt;

    @Column(name = "mod_dt")
    private LocalDateTime modDt;

    public Account() {
    }

    public Account(String firstName, String lastName, String phone,
                   String address, Character isEnabled) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
        this.isEnabled = isEnabled;
        this.createDt = LocalDateTime.now();
        this.modDt = LocalDateTime.now();
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

    public Character getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Character isEnabled) {
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
        return String.format("%-5d | %-8s | %-8s | %-10s | %-22s | %c",
                id != null ? id : 0,
                firstName != null ? firstName.trim() : "",
                lastName != null ? lastName.trim() : "",
                phone != null ? phone.trim() : "",
                address != null ? address.trim() : "",
                isEnabled != null ? isEnabled : ' ');
    }
}
