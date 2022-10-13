package CatBank.Controller;


import CatBank.Model.DTO.FactoryAccountDTO;
import CatBank.Model.DTO.TransferenceDTO;
import CatBank.Model.DTO.UpadteDatesDTO;
import CatBank.Model.User.AccountHolder;
import CatBank.Repository.CheckingRepository;
import CatBank.Repository.CreditCardRepository;
import CatBank.Security.DTO.MensajeDTO;
import CatBank.Security.JasonWebToken.JwtProvider;
import CatBank.Security.Service.RoleService;
import CatBank.Security.Service.UserService;
import CatBank.Service.AccountHolderService;
import CatBank.Service.CreditCardService;
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
@RequestMapping("/creditCard")
@CrossOrigin
public class CreditCardController {

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
    CreditCardService creditCardService;
    @Autowired
    SavingsService savingsService;
    @Autowired
    CreditCardRepository creditCardRepository;

    @PreAuthorize("hasRole('ACCOUNTHOLDER')")
    @GetMapping("/balance/{creditCardId}")
    public ResponseEntity getBalance(@PathVariable(value = "creditCardId") int creditCardId){
        return creditCardService.getBalance(creditCardId);
    }

    @PreAuthorize("hasRoles('ACCOUNTHOLDER','THIRDPARTY')")
    @PostMapping("/transferMoney/")
    public Object creditCardTransferMoney(@Valid @RequestBody TransferenceDTO transferenceDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new MensajeDTO("Los campos introducidos son incorrectos"), HttpStatus.BAD_REQUEST);
        }
        return creditCardService.creditCardTransferMoney(transferenceDTO);
    }

    @PreAuthorize("hasRole('ACCOUNTHOLDER')")//para borrar un Checking, solo un accountholder puede hacerlo
    @DeleteMapping("/deleteCreditCard/{creditCardId}")
    public ResponseEntity<?> deleteCreditCard(@PathVariable("creditCardId") int creditCardId, @RequestBody AccountHolder accountHolder){
        return creditCardService.deleteCreditCard(creditCardId,accountHolder);
    }

    @PreAuthorize("hasRole('ACCOUNTHOLDER')")
    @PatchMapping("/updateCreditCardBalance/{creditCardId}")
    public ResponseEntity<?> udateCreditCard(@PathVariable("creditCardId") int creditCardId, @RequestBody FactoryAccountDTO factoryAccountDTO){
        if (!creditCardService.existsByAccountHolderId(creditCardId)) {
            return new ResponseEntity(new MensajeDTO("La cuenta no está presente"), HttpStatus.NOT_FOUND);
        }
        creditCardService.updateCreditCard(creditCardId, factoryAccountDTO);
        return new ResponseEntity(new MensajeDTO("La cuenta ha sido actualizada"), HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ACCOUNTHOLDER')")
    @PostMapping ("/creditCardInfo")
    public ResponseEntity<?> getCreditCard (@RequestBody AccountHolder accountHolderId){

        return creditCardService.getCreditCard(accountHolderId);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/updateDates/{creditCardId}")
    public ResponseEntity<?> updateDates(@PathVariable("creditCardId") int creditCardId, @RequestBody UpadteDatesDTO upadteDatesDTO){
        if (!creditCardRepository.existsById(creditCardId)) {
            return new ResponseEntity(new MensajeDTO("La cuenta no está presente"), HttpStatus.NOT_FOUND);
        }
        creditCardService.updateDatesCreditCard(creditCardId, upadteDatesDTO);
        return new ResponseEntity(new MensajeDTO("Las fechas han sido actualizadas"), HttpStatus.OK);
    }
}

