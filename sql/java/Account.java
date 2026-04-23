import java.util.Objects;

public class Account {

    private final int id;
    private final String firstName;
    private final String lastName;
    private final String phone;
    private final String address;
    private final String isEnabled;
    private final String createDt;
    private final String modDt;

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

    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getIsEnabled() { return isEnabled; }
    public String getCreateDt() { return createDt; }
    public String getModDt() { return modDt; }

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
