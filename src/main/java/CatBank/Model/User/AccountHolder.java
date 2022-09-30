package CatBank.Model.User;

import CatBank.Model.Checking;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "accountHolder")
public class AccountHolder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int accountHolderId;

    @NotBlank
    private String userName;

    @NotBlank
    private String password;

    @NotNull
    @DateTimeFormat(pattern = "dd-mm-yyyy")
    private Date dateOfBirth;

    @NotBlank
    private String address;

    @NotBlank
    private String email;

    @OneToMany(mappedBy = "accountHolderUser")
    @JsonIgnore
    private List<Checking> chekingsList;

    public AccountHolder(String userName, String password, Date dateOfBirth, String address, String email) {
        this.userName = userName;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.email = email;
    }

    public AccountHolder() {

    }

    //Por defecto crea un usuario normal
    //Si quiero un usuario Admin debo pasar este campo roles
    //private Set<String> roles = new HashSet<>();

    public int getAccountHolderId() {
        return accountHolderId;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public List<Checking> getChekingsList() {
        return chekingsList;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setChekingsList(List<Checking> chekingsList) {
        this.chekingsList = chekingsList;
    }
/*    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

 */
}
