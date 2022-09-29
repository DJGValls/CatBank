package CatBank.Security.Controller;

import CatBank.Security.DTO.*;
import CatBank.Security.JasonWebToken.JwtProvider;
import CatBank.Security.Model.Role;
import CatBank.Security.Model.Enums.RoleName;
import CatBank.Security.Model.User;
import CatBank.Security.Service.RoleService;
import CatBank.Security.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.util.List;
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

    @PostMapping("/newAdmin")
    public ResponseEntity<?> newUser(@Valid @RequestBody NewUserDTO newUserDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new MensajeDTO("Los campos introducidos son incorrectos"), HttpStatus.BAD_REQUEST);
        }
        if (userService.existsByUserName(newUserDTO.getUserName())) {
            return new ResponseEntity<>(new MensajeDTO("El nombre introducido existe o es incorrecto"), HttpStatus.BAD_REQUEST);
        }

        User user = new User(newUserDTO.getUserName(), passwordEncoder.encode(newUserDTO.getPassword()));

        Set<Role> roles = new HashSet<>();
        roles.add((roleService.getByRoleName(RoleName.ROLE_ADMIN).get()));
        roles.add((roleService.getByRoleName(RoleName.ROLE_USER).get()));
        roles.add((roleService.getByRoleName(RoleName.ROLE_ACCOUNTHOLDER).get()));
        if (!newUserDTO.getUserName().contains("admin"))
            return new ResponseEntity<>(new MensajeDTO("admin ha de estar presente en su nombre de usuario"), HttpStatus.BAD_REQUEST);
        user.setRoles(roles);

        userService.save(user);

        return new ResponseEntity<>(new MensajeDTO("Usuario Creado"), HttpStatus.CREATED);
    }

    @PostMapping("/newUserThirtParty")
    public ResponseEntity<?> newUserThirtParty(@Valid @RequestBody NewUserDTO newUserDTO, BindingResult bindingResult) {
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
            return new ResponseEntity<>(new MensajeDTO("nombre de usuario en uso, pruebe una vez más"), HttpStatus.BAD_REQUEST);
        user.setRoles(roles);

        userService.save(user);

        return new ResponseEntity<>(new MensajeDTO("Usuario Creado"), HttpStatus.CREATED);
    }

    @PostMapping("/newUserUserAccountHolder")
    public ResponseEntity<?> newUserAccountHolder(@Valid @RequestBody NewUserAccountHolderDTO newUserAccountHolderDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new MensajeDTO("Los campos introducidos son incorrectos"), HttpStatus.BAD_REQUEST);
        }
        if (userService.existsByUserName(newUserAccountHolderDTO.getUserName())) {
            return new ResponseEntity<>(new MensajeDTO("El nombre introducido es incorrecto"), HttpStatus.BAD_REQUEST);
        }
//        if (userService.existsByEmail(newUserAccountHolderDTO.getEmail())){
//            return new ResponseEntity<>(new MensajeDTO("El mail introducido ya está registrado en otra cuenta"), HttpStatus.BAD_REQUEST);
//        }
        if (!newUserAccountHolderDTO.getEmail().matches("^(.+)@(\\S+)$")){
            return new ResponseEntity<>(new MensajeDTO(" el formato de Email debería ser xxx@yyy.zzz"), HttpStatus.BAD_REQUEST);
        }

        User user = new User(newUserAccountHolderDTO.getUserName()
                ,passwordEncoder.encode(newUserAccountHolderDTO.getPassword())
                ,newUserAccountHolderDTO.getDateOfBirth()
                ,newUserAccountHolderDTO.getAddress()
                ,newUserAccountHolderDTO.getEmail());

        Set<Role> roles = new HashSet<>();
        roles.add((roleService.getByRoleName(RoleName.ROLE_ACCOUNTHOLDER).get()));
        if (newUserAccountHolderDTO.getUserName().contains("admin"))
            return new ResponseEntity<>(new MensajeDTO("nombre de usuario en uso, pruebe una vez más"), HttpStatus.BAD_REQUEST);
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/adminList")
    public List<User> listAdmins(){
        return userService.listAdmins();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/userList")
    public List<User> listUsers(){
        return userService.listUsers();
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
