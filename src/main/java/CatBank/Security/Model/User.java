package CatBank.Security.Model;

import CatBank.Security.Model.Role;
import com.sun.istack.NotNull;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @NotNull
    @Column(unique = true)
    private String userName;

    @NotNull
    private String password;


    //Relación many to many
    //Un usuario puede tener MUCHOS roles y un rol puede PERTENECER a varios usuarios
    //Tabla intermedia que tiene dos campos que va a tener userId y roleId
    // join columns hace referencia a la columna que hace referencia hacia esta
    // Es decir la tabla usuario_rol va a tener un campo que se llama id_usuario
    // inverseJoinColumns = el inverso, hace referencia a rol
    @NotNull
    @ManyToMany
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "user_role"))
    private Set<Role> roles = new HashSet<>();

    public User() {
    }


    //Constuctor sin Id ni Roles
    public User(@NotNull String userName, @NotNull String password) {
        this.userName = userName;
        this.password = password;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
    public Set<Role> getRoles() {
        return roles;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

}