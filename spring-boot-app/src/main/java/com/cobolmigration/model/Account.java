package com.cobolmigration.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.hibernate.type.YesNoConverter;

/**
 * JPA entity mapped to the ACCOUNTS table.
 *
 * <p>Migrated from the COBOL WORKING-STORAGE record defined in
 * {@code sql/sql_example.cbl} (lines 44-52):</p>
 * <pre>
 *   01  ws-sql-account-record.
 *       05  ws-sql-account-id            pic 9(5).
 *       05  ws-sql-account-first-name    pic x(8).
 *       05  ws-sql-account-last-name     pic x(8).
 *       05  ws-sql-account-phone         pic x(10).
 *       05  ws-sql-account-address       pic x(22).
 *       05  ws-sql-account-is-enabled    pic x.
 *       05  ws-sql-account-create-dt     pic x(20).
 *       05  ws-sql-account-mod-dt        pic x(20).
 * </pre>
 */
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "FIRST_NAME", length = 8, nullable = false)
    private String firstName;

    @Column(name = "LAST_NAME", length = 8, nullable = false)
    private String lastName;

    @Column(name = "PHONE", length = 10, nullable = false)
    private String phone;

    @Column(name = "ADDRESS", length = 22, nullable = false)
    private String address;

    /**
     * Maps to the IS_ENABLED column which stores 'Y' or 'N' in the COBOL version.
     * Uses Hibernate's YesNoConverter to translate between boolean and VARCHAR(1).
     */
    @Convert(converter = YesNoConverter.class)
    @Column(name = "IS_ENABLED", nullable = false)
    private boolean enabled;

    @Column(name = "CREATE_DT")
    private LocalDateTime createDt;

    @Column(name = "MOD_DT")
    private LocalDateTime modDt;

    public Account() {
    }

    public Account(String firstName, String lastName, String phone,
                   String address, boolean enabled) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
        this.enabled = enabled;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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
        return "Account{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", enabled=" + enabled +
                ", createDt=" + createDt +
                ", modDt=" + modDt +
                '}';
    }
}
