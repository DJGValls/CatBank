package CatBank.Controller;

import CatBank.Model.Checking;
import CatBank.Model.Enums.Status;
import CatBank.Model.User.AccountHolder;
import CatBank.Model.User.DTO.CheckingDTO;
import CatBank.Security.DTO.MensajeDTO;
import CatBank.Security.JasonWebToken.JwtProvider;
import CatBank.Security.Model.Enums.RoleName;
import CatBank.Security.Model.Role;
import CatBank.Security.Model.User;
import CatBank.Security.Service.RoleService;
import CatBank.Security.Service.UserService;
import CatBank.Service.CheckingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@RestController
//@RequestMapping("/accountHolder")
public class CheckingController {

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

    @PreAuthorize("hasRole('ACCOUNTHOLDER')")
    @PostMapping("/createCheckingTest")
    public Checking create(@RequestBody CheckingDTO checkingDTO){
        Checking checking1 = new Checking(checkingDTO.getPrimaryOwner(),
                checkingDTO.getSecundaryOwner(),
                checkingDTO.getBalance(),
                checkingDTO.getSecretKey(),
                checkingDTO.getStatus(),
                checkingDTO.getAccountHolder());

        checkingService.save(checking1);

        return checking1;
    }

    @PreAuthorize("hasRole('ACCOUNTHOLDER')")
    @PostMapping("/createChecking")//para crear una cuenta checking, solo un accountHolder o un admin con sus tokens pueden hacerlo
    public ResponseEntity<?> createChecking(@Valid @RequestBody CheckingDTO checkingDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new MensajeDTO("Los campos introducidos son incorrectos"), HttpStatus.BAD_REQUEST);
        }
//        if (userService.existsByUserName(accountHolder.getUserName())) {
//            return new ResponseEntity<>(new MensajeDTO("El nombre introducido es incorrecto"), HttpStatus.BAD_REQUEST);
//        }
//        if (userService.existsByEmail(newUserAccountHolderDTO.getEmail())){
//            return new ResponseEntity<>(new MensajeDTO("El mail introducido ya está registrado en otra cuenta"), HttpStatus.BAD_REQUEST);
//        }
//        if (!checking.getEmail().matches("^(.+)@(\\S+)$")){
//            return new ResponseEntity<>(new MensajeDTO(" el formato de Email debería ser xxx@yyy.zzz"), HttpStatus.BAD_REQUEST);
//        }(String primaryOwner, String secundaryOwner, BigDecimal balance, BigDecimal penaltyFee, String secretKey, BigDecimal minBalance, Status status, LocalDateTime creationDate, BigDecimal monthlyMaintenanceFee, AccountHolder accountHolder
        Checking checking1 = new Checking(checkingDTO.getPrimaryOwner(),
                checkingDTO.getSecundaryOwner(),
                checkingDTO.getBalance(),
                checkingDTO.getSecretKey(),
                checkingDTO.getStatus(),
                checkingDTO.getAccountHolder());

        checkingService.save(checking1);

        return new ResponseEntity<>(new MensajeDTO("Cuenta Checking Creada"), HttpStatus.CREATED);
    }

/*
    @PreAuthorize("hasRole('ACCOUNTHOLDER')")
    @PostMapping("/createChecking")//para crear una cuenta checking, solo un accountHolder o un admin con sus tokens pueden hacerlo
    public ResponseEntity<?> createChecking(@Valid @RequestBody Checking checking, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new MensajeDTO("Los campos introducidos son incorrectos"), HttpStatus.BAD_REQUEST);
        }
//        if (userService.existsByUserName(accountHolder.getUserName())) {
//            return new ResponseEntity<>(new MensajeDTO("El nombre introducido es incorrecto"), HttpStatus.BAD_REQUEST);
//        }
//        if (userService.existsByEmail(newUserAccountHolderDTO.getEmail())){
//            return new ResponseEntity<>(new MensajeDTO("El mail introducido ya está registrado en otra cuenta"), HttpStatus.BAD_REQUEST);
//        }
//        if (!checking.getEmail().matches("^(.+)@(\\S+)$")){
//            return new ResponseEntity<>(new MensajeDTO(" el formato de Email debería ser xxx@yyy.zzz"), HttpStatus.BAD_REQUEST);
//        }
        Checking checking1 = new Checking(checking.getPrimaryOwner(),
                checking.getSecundaryOwner(),
                checking.getBalance(),
                checking.getPenaltyFee(),
                passwordEncoder.encode(checking.getSecretKey()),
                checking.getMinBalance(),
                checking.getStatus(),
                checking.getCreationDate(),
                checking.getMonthlyMaintenanceFee(),
                checking.getAccountHolderUser());

        checkingService.save(checking1);

        return new ResponseEntity<>(new MensajeDTO("Cuenta Checking Creada"), HttpStatus.CREATED);
    }


 */
}
