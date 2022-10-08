package CatBank.Controller;

import CatBank.Model.Checking;
import CatBank.Model.DTO.CheckingDTO;
import CatBank.Model.DTO.TransferenceDTO;
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
import java.math.BigDecimal;

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
    public BigDecimal getBalance(@PathVariable(value = "checkingId") int checkingId){
        return checkingService.allFeeApplycations(checkingId);
}

    @PreAuthorize("hasRole('ACCOUNTHOLDER')")
    @PostMapping("/transferenceBetweenCheckings/")
    public Object transferMoneyBetweenCheckings(@Valid @RequestBody TransferenceDTO transferenceDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new MensajeDTO("Los campos introducidos son incorrectos"), HttpStatus.BAD_REQUEST);
        }
        return checkingService.transferMoneyBetweenCheckings(transferenceDTO);
    }
    @PreAuthorize("hasRole('ACCOUNTHOLDER')")//para borrar un Checking, solo un accountholder puede hacerlo
    @DeleteMapping("/deleteChecking/{checkingId}")
    public ResponseEntity<?> deleteChecking(@PathVariable("checkingId") int checkingId, @RequestBody AccountHolder accountHolder){
        if (!checkingService.existsByAccountHolderId(checkingId)) {
            return new ResponseEntity(new MensajeDTO("La cuenta no está presente"), HttpStatus.NOT_FOUND);
        }
        if (!accountHolderService.existsByUserName(accountHolder.getUserName())){
            return new ResponseEntity(new MensajeDTO("El usuario no existe"), HttpStatus.NOT_FOUND);
        }
        if (!checkingRepository.findById(checkingId).get().getAccountHolder().getUserName().equals(accountHolder.getUserName())){
            return new ResponseEntity(new MensajeDTO("Esa cuenta no le pertenece, no puede borrarla"), HttpStatus.BAD_REQUEST);
        }
        checkingService.deleteChecking(checkingId);
        return new ResponseEntity(new MensajeDTO("La cuenta Checking ha sido eliminada"), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ACCOUNTHOLDER')")
    @PatchMapping("/updateChecking/{checkingId}")
    public ResponseEntity<?> udateChecking(@PathVariable("checkingId") int checkingId, @RequestBody CheckingDTO checkingDTOSecundaryOwner){
        if (!checkingService.existsByAccountHolderId(checkingId)) {
            return new ResponseEntity(new MensajeDTO("La cuenta no está presente"), HttpStatus.NOT_FOUND);
        }
        checkingService.updateChecking(checkingId,checkingDTOSecundaryOwner);
        return new ResponseEntity(new MensajeDTO("La cuenta Checking ha sido actualizada"), HttpStatus.OK);
    }


}
