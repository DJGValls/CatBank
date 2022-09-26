package CatBank.Security.Configuration;


import CatBank.Security.Model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;



import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase Encargada de generar la seguridad
 * Clase que implementa los privilegios de cada usuario
 * UserDetails es una clase propia de Spring Security
 */

public class CustomUserDetails implements UserDetails{

    private String user;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;// Coleccion de tipo generico que extendiende de GranthedAuthority de Spring security
    // Variable que nos da la autorización (no confundir con autenticación)

    //Constructor
    public CustomUserDetails(String user, String password, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.password = password;
        this.authorities = authorities;
    }

    //Getters
    public String getCustomUser() {
        return user;
    }


    public String getCustomPassword() {
        return password;
    }


    //Metodo que asigna los privilegios (autorización)
    public static CustomUserDetails build(User user){
        List<GrantedAuthority> authorities = user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
                .collect(Collectors.toList());
        return new CustomUserDetails(user.getUserName(),user.getPassword(),authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return user;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
