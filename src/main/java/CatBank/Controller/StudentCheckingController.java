package CatBank.Controller;

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
@RequestMapping("/studentChecking")
@CrossOrigin
public class StudentCheckingController {
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

    @PreAuthorize("hasRole('ACCOUNTHOLDER')")
    @GetMapping("/balance/{studentCheckingId}")
    public ResponseEntity getBalance(@PathVariable(value = "studentCheckingId") int studentCheckingId){
        return studentCheckingService.getBalance(studentCheckingId);
    }

    @PreAuthorize("hasRole('ACCOUNTHOLDER')")
    @PostMapping("/transferenceBetweenStudentCheckings/")
    public Object transferMoneyBetweenStudentCheckings(@Valid @RequestBody TransferenceDTO transferenceDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new MensajeDTO("Los campos introducidos son incorrectos"), HttpStatus.BAD_REQUEST);
        }
        return studentCheckingService.transferMoneyBetweenStudentCheckings(transferenceDTO);
    }

    @PreAuthorize("hasRole('ACCOUNTHOLDER')")//para borrar un Checking, solo un accountholder puede hacerlo
    @DeleteMapping("/deleteStudentChecking/{studentCheckingId}")
    public ResponseEntity<?> deleteChecking(@PathVariable("studentCheckingId") int studentCheckingId, @RequestBody AccountHolder accountHolder){
        return studentCheckingService.deleteStudentChecking(studentCheckingId,accountHolder);
    }

    @PreAuthorize("hasRole('ACCOUNTHOLDER')")
    @PatchMapping("/updateStudentChecking/{studentCheckingId}")
    public ResponseEntity<?> udateChecking(@PathVariable("studentCheckingId") int studentCheckingId, @RequestBody CheckingDTO checkingDTO){
        if (!studentCheckingService.existsByAccountHolderId(studentCheckingId)) {
            return new ResponseEntity(new MensajeDTO("La cuenta no está presente"), HttpStatus.NOT_FOUND);
        }
        studentCheckingService.updateStudentChecking(studentCheckingId,checkingDTO);
        return new ResponseEntity(new MensajeDTO("La cuenta Checking ha sido actualizada"), HttpStatus.OK);
    }

}
