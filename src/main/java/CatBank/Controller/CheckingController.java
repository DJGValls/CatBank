package CatBank.Controller;

import CatBank.Model.DTO.FactoryAccountDTO;
import CatBank.Model.DTO.TransferenceDTO;
import CatBank.Model.DTO.UpadteDatesDTO;
import CatBank.Model.User.AccountHolder;
import CatBank.Repository.CheckingRepository;
import CatBank.Security.DTO.MensajeDTO;
import CatBank.Security.JasonWebToken.JwtProvider;
import CatBank.Security.Service.RoleService;
import CatBank.Security.Service.UserService;
import CatBank.Service.AccountHolderService;
import CatBank.Service.CheckingService;
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
@RequestMapping("/checking")
@CrossOrigin
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

    @Autowired
    CheckingRepository checkingRepository;

    @Autowired
    AccountHolderService accountHolderService;


    @PreAuthorize("hasRole('ACCOUNTHOLDER')")
    @GetMapping("/balance/{checkingId}")
    public ResponseEntity getBalance(@PathVariable(value = "checkingId") int checkingId){
        return checkingService.allFeeApplycations(checkingId);
    }

    @PreAuthorize("hasRoles('ACCOUNTHOLDER', 'THRIDPARTY')")
    @PostMapping("/transferMoney/")
    public Object transferMoneyChecking(@Valid @RequestBody TransferenceDTO transferenceDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new MensajeDTO("Los campos introducidos son incorrectos"), HttpStatus.BAD_REQUEST);
        }
        return checkingService.checkingTransferMoney(transferenceDTO);
    }
    @PreAuthorize("hasRole('ACCOUNTHOLDER')")
    @DeleteMapping("/deleteChecking/{checkingId}")
    public ResponseEntity<?> deleteChecking(@PathVariable("checkingId") int checkingId, @RequestBody AccountHolder accountHolder){

        return checkingService.deleteChecking(checkingId,accountHolder);
    }
    @PreAuthorize("hasRole('ACCOUNTHOLDER')")
    @PatchMapping("/updateCheckingBalance/{checkingId}")
    public ResponseEntity<?> udateChecking(@PathVariable("checkingId") int checkingId, @RequestBody FactoryAccountDTO factoryAccountDTOBalance){
        if (!checkingRepository.existsById(checkingId)) {
            return new ResponseEntity(new MensajeDTO("La cuenta no está presente"), HttpStatus.NOT_FOUND);
        }
        checkingService.updateChecking(checkingId, factoryAccountDTOBalance);
        return new ResponseEntity(new MensajeDTO("La cuenta Checking ha sido actualizada"), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/updateDates/{checkingId}")
    public ResponseEntity<?> updateDates(@PathVariable("checkingId") int checkingId, @RequestBody UpadteDatesDTO upadteDatesDTO){
        if (!checkingRepository.existsById(checkingId)) {
            return new ResponseEntity(new MensajeDTO("La cuenta no está presente"), HttpStatus.NOT_FOUND);
        }
        checkingService.updateDates(checkingId, upadteDatesDTO);
        return new ResponseEntity(new MensajeDTO("Las fechas han sido actualizadas"), HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ACCOUNTHOLDER')")
    @PostMapping ("/CheckingInfo")
    public ResponseEntity<?> getChecking (@RequestBody AccountHolder accountHolderId){
        return checkingService.getChecking(accountHolderId);
    }
}
