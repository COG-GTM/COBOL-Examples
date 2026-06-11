package com.example.cobolmigration.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * JPA entity that replaces the COBOL {@code 01 ws-sql-account-record} declared in
 * the {@code EXEC SQL BEGIN DECLARE SECTION} of {@code sql/sql_example.cbl}
 * (lines 45-53). The fixed-width PIC clauses become typed columns; the
 * {@code id serial} primary key from {@code sql/create_test_db.sql} maps to an
 * identity-generated {@link Long}.
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

    @Convert(converter = YesNoBooleanConverter.class)
    @Column(name = "is_enabled", nullable = false, length = 1)
    private Boolean isEnabled = Boolean.FALSE;

    @Column(name = "create_dt")
    private LocalDateTime createDt;

    @Column(name = "mod_dt")
    private LocalDateTime modDt;

    public Account() {
    }

    public Account(Long id, String firstName, String lastName, String phone, String address,
                   Boolean isEnabled, LocalDateTime createDt, LocalDateTime modDt) {
        this.id = id;
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

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
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
}
