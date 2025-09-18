package com.cobol.examples.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class AccountRecord {
    private int id;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private boolean isEnabled;
    private LocalDateTime createDateTime;
    private LocalDateTime modDateTime;
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public AccountRecord() {}
    
    public AccountRecord(int id, String firstName, String lastName, String phone, 
                        String address, boolean isEnabled, LocalDateTime createDateTime, 
                        LocalDateTime modDateTime) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
        this.isEnabled = isEnabled;
        this.createDateTime = createDateTime;
        this.modDateTime = modDateTime;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
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
        return isEnabled;
    }
    
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
    
    public LocalDateTime getCreateDateTime() {
        return createDateTime;
    }
    
    public void setCreateDateTime(LocalDateTime createDateTime) {
        this.createDateTime = createDateTime;
    }
    
    public LocalDateTime getModDateTime() {
        return modDateTime;
    }
    
    public void setModDateTime(LocalDateTime modDateTime) {
        this.modDateTime = modDateTime;
    }
    
    public String getEnabledFlag() {
        return isEnabled ? "Y" : "N";
    }
    
    public void setEnabledFlag(String flag) {
        this.isEnabled = "Y".equals(flag);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountRecord that = (AccountRecord) o;
        return id == that.id &&
               isEnabled == that.isEnabled &&
               Objects.equals(firstName, that.firstName) &&
               Objects.equals(lastName, that.lastName) &&
               Objects.equals(phone, that.phone) &&
               Objects.equals(address, that.address) &&
               Objects.equals(createDateTime, that.createDateTime) &&
               Objects.equals(modDateTime, that.modDateTime);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, phone, address, isEnabled, createDateTime, modDateTime);
    }
    
    @Override
    public String toString() {
        return String.format("AccountRecord{id=%d, firstName='%s', lastName='%s', phone='%s', address='%s', enabled=%s}", 
                           id, firstName, lastName, phone, address, getEnabledFlag());
    }
}
