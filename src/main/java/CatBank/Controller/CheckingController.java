package CatBank.Controller;

import CatBank.Model.Checking;
import CatBank.Model.DTO.FactoryAccountDTO;
import CatBank.Model.DTO.ThirdPartyFactoryAccountDTO;
import CatBank.Model.DTO.TransferenceDTO;
import CatBank.Model.DTO.UpadteDatesDTO;
import CatBank.Model.User.AccountHolder;
import CatBank.Repository.CheckingRepository;
import CatBank.Security.DTO.MensajeDTO;
import CatBank.Service.CheckingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/checking")
@CrossOrigin
public class CheckingController {
    @Autowired
    CheckingService checkingService;
    @Autowired
    CheckingRepository checkingRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/accountHolder/createChecking")//para crear una cuenta checking, solo un Admin puede hacerlo
    public ResponseEntity<?> createChecking(@Valid @RequestBody FactoryAccountDTO factoryAccountDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new MensajeDTO("Los campos introducidos son incorrectos"), HttpStatus.BAD_REQUEST);
        }
        return checkingService.createChecking(factoryAccountDTO);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/thirdParty/createChecking")//para crear una cuenta checking para un thirdparty, solo un Admin puede hacerlo
    public ResponseEntity<?> createCheckingThirdParty(@Valid @RequestBody ThirdPartyFactoryAccountDTO thirdPartyFactoryAccountDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new MensajeDTO("Los campos introducidos son incorrectos"), HttpStatus.BAD_REQUEST);
        }
        return checkingService.createCheckingThirdParty(thirdPartyFactoryAccountDTO);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ACCOUNTHOLDER','ROLE_USERTHIRDPARTY')")
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
        return new ResponseEntity(new MensajeDTO("Balance actualizado a " + checkingRepository.findById(checkingId).get().getBalance().getAmount() + " USD"), HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/updateDates/{checkingId}")
    public ResponseEntity<?> updateDates(@PathVariable("checkingId") int checkingId, @RequestBody UpadteDatesDTO upadteDatesDTO){
        if (!checkingRepository.existsById(checkingId)) {
            return new ResponseEntity(new MensajeDTO("La cuenta no está presente"), HttpStatus.NOT_FOUND);
        }
        checkingService.updateDates(checkingId, upadteDatesDTO);
        return new ResponseEntity(new MensajeDTO("Las fechas han sido actualizadas a creation date " + checkingRepository.findById(checkingId).get().getCreationDate() + " y last maintenance date  " + checkingRepository.findById(checkingId).get().getLastMaintenanceAccount()), HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ACCOUNTHOLDER')")
    @PostMapping ("/CheckingInfo")
    public ResponseEntity<?> getChecking (@RequestBody AccountHolder accountHolderId){
        return checkingService.getChecking(accountHolderId);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/checkingList")
    public List<Checking> checkingsList(){
        return checkingService.checkingsList();
    }
}
