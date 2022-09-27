package CatBank.Security.DTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class NewUserAccountHolderDTO {

    @NotBlank
    private String userName;

    @NotBlank
    private String password;

    @NotNull
    private Date dateOfBirth;

    @NotBlank
    private String address;

    @NotBlank
    private String email;

    /*
    public void setEmail(String email) {
        if (email.matches("^(.+)@(\\S+)$")) this.email = email.trim();
        else
            throw new IllegalArgumentException("Emails must follow the xxx@yyy.zzz format ");
    }
     */

    //Por defecto crea un usuario normal
    //Si quiero un usuario Admin debo pasar este campo roles
    private Set<String> roles = new HashSet<>();

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
