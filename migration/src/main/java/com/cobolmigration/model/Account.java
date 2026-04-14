package com.cobolmigration.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Account entity migrated from sql/sql_example.cbl (lines 44-52).
 *
 * COBOL field mappings:
 *   ws-sql-account-id         (pic 9(5))   -> long id
 *   ws-sql-account-first-name (pic x(8))   -> String firstName
 *   ws-sql-account-last-name  (pic x(8))   -> String lastName
 *   ws-sql-account-phone      (pic x(10))  -> String phone
 *   ws-sql-account-address    (pic x(22))  -> String address
 *   ws-sql-account-is-enabled (pic x)      -> boolean enabled
 *   ws-sql-account-create-dt  (pic x(20))  -> LocalDateTime createDate
 *   ws-sql-account-mod-dt     (pic x(20))  -> LocalDateTime modDate
 *
 * The 88-level conditions from the local record (lines 86-87):
 *   ws-account-enabled  value 'Y' -> enabled = true
 *   ws-account-disabled value 'N' -> enabled = false
 */
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "first_name", nullable = false, length = 8)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 8)
    private String lastName;

    @Column(name = "phone", nullable = false, length = 10)
    private String phone;

    @Column(name = "address", nullable = false, length = 22)
    private String address;

    @Column(name = "is_enabled", nullable = false, length = 1)
    private boolean enabled;

    @Column(name = "create_dt")
    private LocalDateTime createDate;

    @Column(name = "mod_dt")
    private LocalDateTime modDate;

    public Account() {
    }

    public Account(long id, String firstName, String lastName, String phone,
                   String address, boolean enabled, LocalDateTime createDate,
                   LocalDateTime modDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
        this.enabled = enabled;
        this.createDate = createDate;
        this.modDate = modDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getModDate() {
        return modDate;
    }

    public void setModDate(LocalDateTime modDate) {
        this.modDate = modDate;
    }

    /**
     * Returns the COBOL-style enabled flag character.
     * Maps to the 88-level conditions: 'Y' for enabled, 'N' for disabled.
     */
    public String getEnabledFlag() {
        return enabled ? "Y" : "N";
    }

    /**
     * Sets the enabled state from a COBOL-style flag character.
     * Accepts 'Y' for enabled, anything else for disabled.
     */
    public void setEnabledFromFlag(String flag) {
        this.enabled = "Y".equalsIgnoreCase(flag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id == account.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Account{id=%d, firstName='%s', lastName='%s', phone='%s', address='%s', enabled=%s}",
                id, firstName, lastName, phone, address, enabled);
    }
}
