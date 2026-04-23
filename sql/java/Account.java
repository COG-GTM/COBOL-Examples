/**
 * Account POJO representing a row in the accounts table.
 * Mirrors the COBOL ws-account-record structure.
 */
public class Account {

    private int id;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String isEnabled;
    private String createDt;
    private String modDt;

    public Account(int id, String firstName, String lastName, String phone,
                   String address, String isEnabled, String createDt, String modDt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
        this.isEnabled = isEnabled;
        this.createDt = createDt;
        this.modDt = modDt;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getIsEnabled() {
        return isEnabled;
    }

    public String getCreateDt() {
        return createDt;
    }

    public String getModDt() {
        return modDt;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", isEnabled='" + isEnabled + '\'' +
                ", createDt='" + createDt + '\'' +
                ", modDt='" + modDt + '\'' +
                '}';
    }
}
