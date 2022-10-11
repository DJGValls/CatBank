package CatBank.Controller;

import CatBank.Model.DTO.FactoryAccountDTO;
import CatBank.Model.DTO.TransferenceDTO;
import CatBank.Model.User.AccountHolder;
import CatBank.Repository.CheckingRepository;
import CatBank.Security.DTO.MensajeDTO;
import CatBank.Security.JasonWebToken.JwtProvider;
import CatBank.Security.Service.RoleService;
import CatBank.Security.Service.UserService;
import CatBank.Service.AccountHolderService;
import CatBank.Service.SavingsService;
import CatBank.Service.StudentCheckingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/savings")
@CrossOrigin
public class SavingsController {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Autowired
    StudentCheckingService studentCheckingService;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    CheckingRepository checkingRepository;

    @Autowired
    AccountHolderService accountHolderService;

    @Autowired
    SavingsService savingsService;

    @PreAuthorize("hasRole('ACCOUNTHOLDER')")
    @GetMapping("/balance/{savingsId}")
    public ResponseEntity getBalance(@PathVariable(value = "savingsId") int savingsId){
        return savingsService.getBalance(savingsId);
    }

    @PreAuthorize("hasRole('ACCOUNTHOLDER')")
    @PostMapping("/transferMoney/")
    public Object savingsTransferMoney(@Valid @RequestBody TransferenceDTO transferenceDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new MensajeDTO("Los campos introducidos son incorrectos"), HttpStatus.BAD_REQUEST);
        }
        return savingsService.SavingsTransferMoney(transferenceDTO);
    }

    @PreAuthorize("hasRole('ACCOUNTHOLDER')")//para borrar un Checking, solo un accountholder puede hacerlo
    @DeleteMapping("/deleteSavings/{savingsId}")
    public ResponseEntity<?> deleteSavings(@PathVariable("savingsId") int savingsId, @RequestBody AccountHolder accountHolder){
        return savingsService.deleteSavings(savingsId,accountHolder);
    }

    @PreAuthorize("hasRole('ACCOUNTHOLDER')")
    @PatchMapping("/updateSavings/{savingsId}")
    public ResponseEntity<?> udateSavings(@PathVariable("savingsId") int savingsId, @RequestBody FactoryAccountDTO factoryAccountDTO){
        if (!savingsService.existsByAccountHolderId(savingsId)) {
            return new ResponseEntity(new MensajeDTO("La cuenta no está presente"), HttpStatus.NOT_FOUND);
        }
        savingsService.updateSavings(savingsId, factoryAccountDTO);
        return new ResponseEntity(new MensajeDTO("La cuenta Checking ha sido actualizada"), HttpStatus.OK);
    }


}
