package CatBank.Controller;

import CatBank.Model.Checking;
import CatBank.Model.CreditCard;
import CatBank.Model.Savings;
import CatBank.Model.StudentChecking;
import CatBank.Model.User.AccountHolder;
import CatBank.Model.User.ThirdParty;
import CatBank.Security.DTO.JwtDTO;
import CatBank.Security.DTO.MensajeDTO;
import CatBank.Security.DTO.NewUserDTO;
import CatBank.Security.DTO.UserLoginDTO;
import CatBank.Security.Model.User;
import CatBank.Security.Service.AdminService;
import CatBank.Security.Service.UserService;
import CatBank.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    AccountHolderService accountHolderService;
    @Autowired
    UserService userService;
    @Autowired
    CheckingService checkingService;
    @Autowired
    SavingsService savingsService;
    @Autowired
    StudentCheckingService studentCheckingService;
    @Autowired
    CreditCardService creditCardService;
    @Autowired
    ThirdPartyService thirdPartyService;
    @Autowired
    AdminService adminService;


    @PostMapping("/login")//para obtener un token, ya sea de admin, de accountHolder o de thirdParty
    public ResponseEntity<JwtDTO> login(@Valid @RequestBody UserLoginDTO userLoginDTO, BindingResult bindingResult){
        if (bindingResult.hasErrors())
            return new ResponseEntity(new MensajeDTO("Los campos introducidos son incorrectos"), HttpStatus.BAD_REQUEST);
        return adminService.loginGenerator(userLoginDTO);
    }
    @PostMapping("/newAdmin")//para crear un nuevo admin, no necesitas token
    public ResponseEntity<?> newUser(@Valid @RequestBody NewUserDTO newUserDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new MensajeDTO("Los campos introducidos son incorrectos"), HttpStatus.BAD_REQUEST);
        }
       return adminService.createAdminUser(newUserDTO);
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
