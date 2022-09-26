package CatBank.Security.DTO;


import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

public class NewUserDTO {

    @NotBlank
    private String userName;

    @NotBlank
    private String password;

    //Por defecto crea un usuario normal
    //Si quiero un usuario Admin debo pasar este campo roles
    private Set<String> roles = new HashSet<>();


    //Getter y setter
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

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
