package CatBank.Controller;

import CatBank.Model.Checking;
import CatBank.Model.CreditCard;
import CatBank.Model.DTO.ThirdPartyFactoryAccountDTO;
import CatBank.Model.Savings;
import CatBank.Model.StudentChecking;
import CatBank.Model.User.AccountHolder;
import CatBank.Model.DTO.FactoryAccountDTO;
import CatBank.Model.User.ThirdParty;
import CatBank.Security.DTO.JwtDTO;
import CatBank.Security.DTO.MensajeDTO;
import CatBank.Security.DTO.NewUserDTO;
import CatBank.Security.DTO.UserLoginDTO;
import CatBank.Security.JasonWebToken.JwtProvider;
import CatBank.Security.Model.Enums.RoleName;
import CatBank.Security.Model.Role;
import CatBank.Security.Model.User;
import CatBank.Security.Service.RoleService;
import CatBank.Security.Service.UserService;
import CatBank.Service.*;
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
    @Autowired
    SavingsService savingsService;
    @Autowired
    StudentCheckingService studentCheckingService;
    @Autowired
    CreditCardService creditCardService;
    @Autowired
    ThirdPartyService thirdPartyService;

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
    public ResponseEntity<?> newUserThirdParty(@Valid @RequestBody ThirdParty thirdParty, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new MensajeDTO("Los campos introducidos son incorrectos"), HttpStatus.BAD_REQUEST);
        }return thirdPartyService.createThirdParty(thirdParty);
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
            new ResponseEntity<>(new MensajeDTO("Los campos introducidos son incorrectos"), HttpStatus.BAD_REQUEST);
        }
        return accountHolderService.createAccountHolder(accountHolder);
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
    @PostMapping("/accountHolder/createChecking")//para crear una cuenta checking, solo un Admin puede hacerlo
    public ResponseEntity<?> createChecking(@Valid @RequestBody FactoryAccountDTO factoryAccountDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new MensajeDTO("Los campos introducidos son incorrectos"), HttpStatus.BAD_REQUEST);
        }
        return checkingService.createChecking(factoryAccountDTO);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/thirdParty/createChecking")//para crear una cuenta checking, solo un Admin puede hacerlo
    public ResponseEntity<?> createCheckingThirdParty(@Valid @RequestBody ThirdPartyFactoryAccountDTO thirdPartyFactoryAccountDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new MensajeDTO("Los campos introducidos son incorrectos"), HttpStatus.BAD_REQUEST);
        }
        return checkingService.createCheckingThirdParty(thirdPartyFactoryAccountDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createSavings")//para crear una cuenta checking, solo un Admin puede hacerlo
    public ResponseEntity<?> createSavings(@Valid @RequestBody FactoryAccountDTO factoryAccountDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new MensajeDTO("Los campos introducidos son incorrectos"), HttpStatus.BAD_REQUEST);
        }
        return savingsService.createSaving(factoryAccountDTO);
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
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/studentCheckingList")
    public List<StudentChecking> studentCheckingsList(){
        return studentCheckingService.studentCheckingsList();
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/savingsList")
    public List<Savings> savingsList(){
        return savingsService.savingsList();
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/creditCardsList")
    public List<CreditCard> creditCardsList(){
        return creditCardService.creditCardList();
    }
}
