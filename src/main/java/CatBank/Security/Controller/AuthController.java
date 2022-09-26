package CatBank.Security.Controller;

import CatBank.Security.DTO.JwtDTO;
import CatBank.Security.DTO.MensajeDTO;
import CatBank.Security.DTO.NewUserDTO;
import CatBank.Security.DTO.UserLoginDTO;
import CatBank.Security.JasonWebToken.JwtProvider;
import CatBank.Security.Model.Role;
import CatBank.Security.Model.RoleName;
import CatBank.Security.Model.User;
import CatBank.Security.Service.RoleService;
import CatBank.Security.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Autowired
    JwtProvider jwtProvider;

    @PostMapping("/newUser")
    public ResponseEntity<?> newUser(@Valid @RequestBody NewUserDTO newUserDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new MensajeDTO("Los campos introducidos son incorrectos"), HttpStatus.BAD_REQUEST);
        }
        if (userService.existsByUserName(newUserDTO.getUserName())) {
            return new ResponseEntity<>(new MensajeDTO("El nombre introducido es incorrecto"), HttpStatus.BAD_REQUEST);
        }

        User user = new User(newUserDTO.getUserName(), passwordEncoder.encode(newUserDTO.getPassword()));

        Set<Role> roles = new HashSet<>();
        roles.add((roleService.getByRoleName(RoleName.ROLE_USER).get()));
        if (newUserDTO.getUserName().contains("admin"))
            roles.add(roleService.getByRoleName(RoleName.ROLE_ADMIN).get());
        user.setRoles(roles);

        userService.save(user);

        return new ResponseEntity<>(new MensajeDTO("Usuario Creado"), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtDTO> login(@Valid @RequestBody UserLoginDTO userLoginDTO, BindingResult bindingResult){
        if (bindingResult.hasErrors())
            return new ResponseEntity(new MensajeDTO("Los campos introducidos son incorrectos"), HttpStatus.BAD_REQUEST);

        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginDTO.getUserName(),
                        userLoginDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateToken(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        JwtDTO jwtDTO = new JwtDTO(jwt, userDetails.getUsername(), userDetails.getAuthorities());
        return new ResponseEntity<>(jwtDTO, HttpStatus.OK);

    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping({ "/helloUser" })
    public String testUser() {
        return "Hello Token de USER";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping({ "/helloAdmin" })
    public String testAdmin() {
        return "Hello Token de ADMIN";
    }



}
