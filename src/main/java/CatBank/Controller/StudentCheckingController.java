package CatBank.Controller;

import CatBank.Model.DTO.FactoryAccountDTO;
import CatBank.Model.DTO.TransferenceDTO;
import CatBank.Model.StudentChecking;
import CatBank.Model.User.AccountHolder;
import CatBank.Repository.StudentCheckingRepository;
import CatBank.Security.DTO.MensajeDTO;
import CatBank.Service.StudentCheckingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/studentChecking")
@CrossOrigin
public class StudentCheckingController {


    @Autowired
    StudentCheckingService studentCheckingService;
    @Autowired
    StudentCheckingRepository studentCheckingRepository;


    @PreAuthorize("hasRole('ACCOUNTHOLDER')")
    @GetMapping("/balance/{studentCheckingId}")
    public ResponseEntity getBalance(@PathVariable(value = "studentCheckingId") int studentCheckingId){
        return studentCheckingService.getBalance(studentCheckingId);
    }
    @PreAuthorize("hasAnyAuthority('ROLE_ACCOUNTHOLDER','ROLE_USERTHIRDPARTY')")
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
        return new ResponseEntity(new MensajeDTO("La cuenta Checking ha sido actualizada, el saldo actual es de " + studentCheckingRepository.findById(studentCheckingId).get().getBalance().getAmount()), HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ACCOUNTHOLDER')")
    @PostMapping ("/studentCheckingInfo")
    public ResponseEntity<?> getStudentChecking (@RequestBody AccountHolder accountHolderId){

        return studentCheckingService.getStudentChecking(accountHolderId);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/studentCheckingList")
    public List<StudentChecking> studentCheckingsList(){
        return studentCheckingService.studentCheckingsList();
    }

}
