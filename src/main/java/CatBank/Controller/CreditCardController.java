package CatBank.Controller;


import CatBank.Model.CreditCard;
import CatBank.Model.DTO.FactoryAccountDTO;
import CatBank.Model.DTO.TransferenceDTO;
import CatBank.Model.DTO.UpadteDatesDTO;
import CatBank.Model.User.AccountHolder;
import CatBank.Repository.CreditCardRepository;
import CatBank.Security.DTO.MensajeDTO;
import CatBank.Service.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/creditCard")
@CrossOrigin
public class CreditCardController {


    @Autowired
    CreditCardService creditCardService;
    @Autowired
    CreditCardRepository creditCardRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createCreditCard")//para crear una cuenta checking, solo un Admin puede hacerlo
    public ResponseEntity<?> createCreditCard(@Valid @RequestBody FactoryAccountDTO factoryAccountDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new MensajeDTO("Los campos introducidos son incorrectos"), HttpStatus.BAD_REQUEST);
        }
        return creditCardService.createCreditCard(factoryAccountDTO);
    }
    @PreAuthorize("hasRole('ACCOUNTHOLDER')")
    @GetMapping("/balance/{creditCardId}")
    public ResponseEntity getBalance(@PathVariable(value = "creditCardId") int creditCardId){
        return creditCardService.getBalance(creditCardId);
    }
    @PreAuthorize("hasAnyAuthority('ROLE_ACCOUNTHOLDER','ROLE_USERTHIRDPARTY')")
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
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/creditCardsList")
    public List<CreditCard> creditCardsList(){
        return creditCardService.creditCardList();
    }
}

