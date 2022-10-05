package CatBank.Controller;

import CatBank.Model.Checking;
import CatBank.Model.User.AccountHolder;
import CatBank.Model.User.DTO.CheckingDTO;
import CatBank.Security.DTO.*;
import CatBank.Security.JasonWebToken.JwtProvider;
import CatBank.Security.Model.Role;
import CatBank.Security.Model.Enums.RoleName;
import CatBank.Security.Model.User;
import CatBank.Security.Service.RoleService;
import CatBank.Security.Service.UserService;
import CatBank.Service.AccountHolderService;
import CatBank.Service.CheckingService;
import CatBank.Utils.Money;
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
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
    @PreAuthorize("hasRole('ADMIN')")//para borrar un user admin, thirdparty o accountholder. Solo un admin puede hacerlo
    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") int userId){
        if (!userService.existsByUserId(userId))
            return new ResponseEntity(new MensajeDTO("No existe ese usuario"), HttpStatus.NOT_FOUND);
        userService.deleteUser(userId);
        return new ResponseEntity(new MensajeDTO("Usuario eliminado"), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/newUserAccountHolder")//para crear una accountholder, solo un admin con su token puede hacerlo
    public ResponseEntity<?> newUserAccountHolder(@Valid @RequestBody AccountHolder accountHolder, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new MensajeDTO("Los campos introducidos son incorrectos"), HttpStatus.BAD_REQUEST);
        }
        if (userService.existsByUserName(accountHolder.getUserName())) {
            return new ResponseEntity<>(new MensajeDTO(accountHolder.getUserName() + " está en uso, cambie su nombre de usario"), HttpStatus.BAD_REQUEST);
        }
        if (accountHolderService.existByEmail(accountHolder.getEmail())){
            return new ResponseEntity<>(new MensajeDTO(accountHolder.getEmail() + " ya está en uso"), HttpStatus.BAD_REQUEST);
        }
        if (!accountHolder.getEmail().matches("^(.+)@(\\S+)$")){
            return new ResponseEntity<>(new MensajeDTO(" el formato de Email debería ser xxx@yyy.zzz"), HttpStatus.BAD_REQUEST);
        }

        User user = new User(accountHolder.getUserName(), passwordEncoder.encode(accountHolder.getPassword()));
        Set<Role> roles = new HashSet<>();
        roles.add((roleService.getByRoleName(RoleName.ROLE_ACCOUNTHOLDER).get()));
        if (accountHolder.getUserName().contains("admin"))
            return new ResponseEntity<>(new MensajeDTO("nombre de usuario no puede contener la palabra admin, pruebe una vez más"), HttpStatus.BAD_REQUEST);
        user.setRoles(roles);
        userService.save(user);

        AccountHolder accountHolder1 = new AccountHolder(accountHolder.getUserName()
                ,passwordEncoder.encode(accountHolder.getPassword())
                , accountHolder.getDateOfBirth()
                , accountHolder.getAddress()
                , accountHolder.getEmail());
        accountHolderService.save(accountHolder1);

                return new ResponseEntity<>(new MensajeDTO("Usuario AccountHolder Creado"), HttpStatus.CREATED);
    }
    @PreAuthorize("hasRole('ADMIN')")//para borrar un accountholder, solo un admin puede hacerlo
    @DeleteMapping("/deleteAccountHolder/{accountHolderId}")
    public ResponseEntity<?> delete(@PathVariable("accountHolderId") int accountHolderId){
        if (!accountHolderService.existsByAccountHolderId(accountHolderId))
            return new ResponseEntity(new MensajeDTO("No existe ese usuario"), HttpStatus.NOT_FOUND);
        accountHolderService.delete(accountHolderId);
        return new ResponseEntity(new MensajeDTO("Usuario eliminado"), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createChecking")//para crear una cuenta checking, solo un Admin puede hacerlo
    public ResponseEntity<?> createChecking(@Valid @RequestBody CheckingDTO checkingDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new MensajeDTO("Los campos introducidos son incorrectos"), HttpStatus.BAD_REQUEST);
        }
        if (!accountHolderService.existsByAccountHolderId(checkingDTO.getAccountHolder().getAccountHolderId())||!accountHolderService.existByEmail(checkingDTO.getAccountHolder().getEmail())){
            return new ResponseEntity<>(new MensajeDTO("El usuario " + checkingDTO.getAccountHolder().getUserName() + " no existe, revise si ha sido creado y que su id y sus datos estén correctos"), HttpStatus.BAD_REQUEST);
        }
        if (checkingService.existsByPrimaryOwner(checkingDTO.getPrimaryOwner())){
            return new ResponseEntity<>(new MensajeDTO("El usuario " + checkingDTO.getAccountHolder().getUserName() + " ya tiene una cuenta Checking creada, revise que los datos sean correctos"), HttpStatus.BAD_REQUEST);
        }
        LocalDate start = LocalDate.from(checkingDTO.getAccountHolder().getDateOfBirth());
        LocalDate end = LocalDate.now();
        long years = ChronoUnit.YEARS.between(start, end);
        if(years < 24){
            return new ResponseEntity<>(new MensajeDTO("El usuario " + checkingDTO.getAccountHolder().getUserName() + " es menor de 24 años, solo las cuentas StudentChecking están disponibles para ese rango de edad" ), HttpStatus.BAD_REQUEST);
        }


        Checking checking1 = new Checking(checkingDTO.getPrimaryOwner(),
                checkingDTO.getSecundaryOwner(),
                new Money(new BigDecimal(checkingDTO.getBalance().getAmount(), new MathContext(6, RoundingMode.HALF_EVEN)),
                        Currency.getInstance(checkingDTO.getBalance().getCurrencyCode())),
                checkingDTO.getSecretKey(),
                checkingDTO.getStatus(),
                checkingDTO.getAccountHolder());

        checkingService.save(checking1);

        return new ResponseEntity<>(new MensajeDTO("Cuenta Checking Creada"), HttpStatus.CREATED);
    }
    @PreAuthorize("hasRole('ADMIN')")//para borrar un Checking, solo un admin puede hacerlo
    @DeleteMapping("/deleteChecking/{checkingId}")
    public ResponseEntity<?> deleteChecking(@PathVariable("checkingId") int checkingId){
        if (!checkingService.existsByAccountHolderId(checkingId))
            return new ResponseEntity(new MensajeDTO("No existe esa cuenta checking"), HttpStatus.NOT_FOUND);
        checkingService.deleteChecking(checkingId);
        return new ResponseEntity(new MensajeDTO("La cuenta Checking ha sido eliminada"), HttpStatus.OK);
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/accountHolderList")
    public List<AccountHolder> accountHoldersList(){
        return accountHolderService.accountHoldersList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/checkingList")
    public List<Checking> checkingsList(){
        return checkingService.checkingsList();
    }






}
