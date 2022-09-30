package CatBank.Controller;

import CatBank.Model.User.AccountHolder;
import CatBank.Security.DTO.*;
import CatBank.Security.JasonWebToken.JwtProvider;
import CatBank.Security.Model.Role;
import CatBank.Security.Model.Enums.RoleName;
import CatBank.Security.Model.User;
import CatBank.Security.Service.RoleService;
import CatBank.Security.Service.UserService;
import CatBank.Service.AccountHolderService;
import CatBank.Service.CheckingService;
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
    AccountHolderService accountHolderService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Autowired
    CheckingService checkingService;

    @Autowired
    JwtProvider jwtProvider;

    @PostMapping("/newAdmin")//para crear un nuevo admin, no necesitas token
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
        roles.add((roleService.getByRoleName(RoleName.ROLE_USERTHIRDPARTY).get()));
        roles.add((roleService.getByRoleName(RoleName.ROLE_ACCOUNTHOLDER).get()));
        if (!newUserDTO.getUserName().contains("admin"))
            return new ResponseEntity<>(new MensajeDTO("admin ha de estar presente en su nombre de usuario"), HttpStatus.BAD_REQUEST);
        user.setRoles(roles);

        userService.save(user);

        return new ResponseEntity<>(new MensajeDTO("Usuario Creado"), HttpStatus.CREATED);
    }
    @PostMapping("/login")//para obtener un token, ya sea de admin, de accountHolder o de thirdParty
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
    @PostMapping("/newUserThirdParty")//para crear un user thirdParty, solo un admin con su token puede hacerlo
    public ResponseEntity<?> newUserThirdParty(@Valid @RequestBody NewUserDTO newUserDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new MensajeDTO("Los campos introducidos son incorrectos"), HttpStatus.BAD_REQUEST);
        }
        if (userService.existsByUserName(newUserDTO.getUserName())) {
            return new ResponseEntity<>(new MensajeDTO("El nombre introducido es incorrecto"), HttpStatus.BAD_REQUEST);
        }

        User user = new User(newUserDTO.getUserName(), passwordEncoder.encode(newUserDTO.getPassword()));

        Set<Role> roles = new HashSet<>();
        roles.add((roleService.getByRoleName(RoleName.ROLE_USERTHIRDPARTY).get()));
        if (newUserDTO.getUserName().contains("admin"))
            return new ResponseEntity<>(new MensajeDTO("nombre de usuario en uso, pruebe una vez más"), HttpStatus.BAD_REQUEST);
        user.setRoles(roles);

        userService.save(user);


        return new ResponseEntity<>(new MensajeDTO("Usuario Creado"), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/newUserAccountHolder")//para crear un user accountholder, solo un admin con su token puede hacerlo
    public ResponseEntity<?> newUserAccountHolder(@Valid @RequestBody AccountHolder accountHolder, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new MensajeDTO("Los campos introducidos son incorrectos"), HttpStatus.BAD_REQUEST);
        }
        if (userService.existsByUserName(accountHolder.getUserName())) {
            return new ResponseEntity<>(new MensajeDTO("El nombre introducido es incorrecto"), HttpStatus.BAD_REQUEST);
        }
//        if (userService.existsByEmail(newUserAccountHolderDTO.getEmail())){
//            return new ResponseEntity<>(new MensajeDTO("El mail introducido ya está registrado en otra cuenta"), HttpStatus.BAD_REQUEST);
//        }
        if (!accountHolder.getEmail().matches("^(.+)@(\\S+)$")){
            return new ResponseEntity<>(new MensajeDTO(" el formato de Email debería ser xxx@yyy.zzz"), HttpStatus.BAD_REQUEST);
        }

        User user = new User(accountHolder.getUserName(), passwordEncoder.encode(accountHolder.getPassword()));
        Set<Role> roles = new HashSet<>();
        roles.add((roleService.getByRoleName(RoleName.ROLE_ACCOUNTHOLDER).get()));
        if (accountHolder.getUserName().contains("admin"))
            return new ResponseEntity<>(new MensajeDTO("nombre de usuario en uso, pruebe una vez más"), HttpStatus.BAD_REQUEST);
        user.setRoles(roles);
        userService.save(user);

        AccountHolder accountHolder1 = new AccountHolder(accountHolder.getUserName()
                ,passwordEncoder.encode(accountHolder.getPassword())
                , accountHolder.getDateOfBirth()
                , accountHolder.getAddress()
                , accountHolder.getEmail());
        accountHolderService.save(accountHolder1);

                return new ResponseEntity<>(new MensajeDTO("Usuario Creado"), HttpStatus.CREATED);
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


    @PreAuthorize("hasRole('ACCOUNTHOLDER')")
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
