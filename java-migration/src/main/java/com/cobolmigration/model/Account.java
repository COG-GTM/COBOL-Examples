package com.cobolmigration.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * JPA Entity mapping the ACCOUNTS table from sql/sql_example.cbl lines 44-52.
 * Also used as the Flyway-managed schema from sql/create_test_db.sql.
 */
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name", nullable = false, length = 8)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 8)
    private String lastName;

    @Column(name = "phone", nullable = false, length = 10)
    private String phone;

    @Column(name = "address", nullable = false, length = 22)
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
        this.createDt = LocalDateTime.now();
        this.modDt = LocalDateTime.now();
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
        return String.format("%-5s | %-8s | %-8s | %-10s | %-22s | %s",
                id != null ? id : "",
                firstName != null ? firstName : "",
                lastName != null ? lastName : "",
                phone != null ? phone : "",
                address != null ? address : "",
                isEnabled != null ? isEnabled : "");
    }
}
