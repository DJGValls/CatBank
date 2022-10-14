package CatBank.Controller;

import CatBank.Model.DTO.FactoryAccountDTO;
import CatBank.Model.DTO.TransferenceDTO;
import CatBank.Model.User.AccountHolder;
import CatBank.Security.DTO.MensajeDTO;
import CatBank.Service.StudentCheckingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/studentChecking")
@CrossOrigin
public class StudentCheckingController {


    @Autowired
    StudentCheckingService studentCheckingService;


    @PreAuthorize("hasRole('ACCOUNTHOLDER')")
    @GetMapping("/balance/{studentCheckingId}")
    public ResponseEntity getBalance(@PathVariable(value = "studentCheckingId") int studentCheckingId){
        return studentCheckingService.getBalance(studentCheckingId);
    }
    @PreAuthorize("hasRoles('ACCOUNTHOLDER','USERTHIRDPARTY')")
    @PostMapping("/transferMoney/")
    public Object studentCheckingTransferMoney(@Valid @RequestBody TransferenceDTO transferenceDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new MensajeDTO("Los campos introducidos son incorrectos"), HttpStatus.BAD_REQUEST);
        }
        return studentCheckingService.studentCheckingTransferMoney(transferenceDTO);
    }
    @PreAuthorize("hasRole('ACCOUNTHOLDER')")//para borrar un Checking, solo un accountholder puede hacerlo
    @DeleteMapping("/deleteStudentChecking/{studentCheckingId}")
    public ResponseEntity<?> deleteChecking(@PathVariable("studentCheckingId") int studentCheckingId, @RequestBody AccountHolder accountHolder){
        return studentCheckingService.deleteStudentChecking(studentCheckingId,accountHolder);
    }
    @PreAuthorize("hasRole('ACCOUNTHOLDER')")
    @PatchMapping("/updateStudentCheckingBalance/{studentCheckingId}")
    public ResponseEntity<?> udateChecking(@PathVariable("studentCheckingId") int studentCheckingId, @RequestBody FactoryAccountDTO factoryAccountDTO){
        if (!studentCheckingService.existsByAccountHolderId(studentCheckingId)) {
            return new ResponseEntity(new MensajeDTO("La cuenta no está presente"), HttpStatus.NOT_FOUND);
        }
        studentCheckingService.updateStudentChecking(studentCheckingId, factoryAccountDTO);
        return new ResponseEntity(new MensajeDTO("La cuenta ha sido actualizada"), HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ACCOUNTHOLDER')")
    @PostMapping ("/studentCheckingInfo")
    public ResponseEntity<?> getStudentChecking (@RequestBody AccountHolder accountHolderId){

        return studentCheckingService.getStudentChecking(accountHolderId);
    }

}
