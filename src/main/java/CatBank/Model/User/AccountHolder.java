package CatBank.Model.User;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

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
    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(name = "date_of_birth", updatable = false, nullable = false)
    //@Temporal(TemporalType.DATE)
    private LocalDate dateOfBirth;

    @Embedded
    @Nullable
    private AccountHolderAddress address;

    @Nullable
    private String email;



    public AccountHolder(String userName, String password, LocalDate dateOfBirth, AccountHolderAddress address, String email) {
        this.userName = userName;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.email = email;
    }


    public AccountHolder() {

    }

    public AccountHolder(String userName) {
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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public AccountHolderAddress getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }



    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setAddress(AccountHolderAddress address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }


/*    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

 */
}
