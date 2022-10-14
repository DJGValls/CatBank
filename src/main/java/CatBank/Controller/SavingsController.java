package CatBank.Controller;

import CatBank.Model.DTO.FactoryAccountDTO;
import CatBank.Model.DTO.TransferenceDTO;
import CatBank.Model.DTO.UpadteDatesDTO;
import CatBank.Model.Savings;
import CatBank.Model.User.AccountHolder;
import CatBank.Repository.SavingsRepository;
import CatBank.Security.DTO.MensajeDTO;
import CatBank.Service.SavingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/savings")
@CrossOrigin
public class SavingsController {


    @Autowired
    SavingsService savingsService;
    @Autowired
    SavingsRepository savingsRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createSavings")//para crear una cuenta checking, solo un Admin puede hacerlo
    public ResponseEntity<?> createSavings(@Valid @RequestBody FactoryAccountDTO factoryAccountDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new MensajeDTO("Los campos introducidos son incorrectos"), HttpStatus.BAD_REQUEST);
        }
        return savingsService.createSaving(factoryAccountDTO);
    }
    @PreAuthorize("hasRole('ACCOUNTHOLDER')")
    @GetMapping("/balance/{savingsId}")
    public ResponseEntity getBalance(@PathVariable(value = "savingsId") int savingsId){
        return savingsService.getBalance(savingsId);
    }
    @PreAuthorize("hasRoles('ACCOUNTHOLDER','USERTHIRDPARTY')")
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
    @PatchMapping("/updateSavingsBalance/{savingsId}")
    public ResponseEntity<?> udateSavings(@PathVariable("savingsId") int savingsId, @RequestBody FactoryAccountDTO factoryAccountDTO){
        if (!savingsService.existsByAccountHolderId(savingsId)) {
            return new ResponseEntity(new MensajeDTO("La cuenta no está presente"), HttpStatus.NOT_FOUND);
        }
        savingsService.updateSavings(savingsId, factoryAccountDTO);
        return new ResponseEntity(new MensajeDTO("La cuenta Checking ha sido actualizada"), HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ACCOUNTHOLDER')")
    @PostMapping ("/savingsInfo")
    public ResponseEntity<?> getSavings (@RequestBody AccountHolder accountHolderId){

        return savingsService.getSavings(accountHolderId);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/updateDates/{savingsId}")
    public ResponseEntity<?> updateDates(@PathVariable("savingsId") int savingsId, @RequestBody UpadteDatesDTO upadteDatesDTO){
        if (!savingsRepository.existsById(savingsId)) {
            return new ResponseEntity(new MensajeDTO("La cuenta no está presente"), HttpStatus.NOT_FOUND);
        }
        savingsService.updateDates(savingsId, upadteDatesDTO);
        return new ResponseEntity(new MensajeDTO("Las fechas han sido actualizadas"), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/savingsList")
    public List<Savings> savingsList(){
        return savingsService.savingsList();
    }


}
