package CatBank.Controller;

import CatBank.Model.DTO.TransferenceDTO;
import CatBank.Repository.CheckingRepository;
import CatBank.Security.DTO.MensajeDTO;
import CatBank.Security.JasonWebToken.JwtProvider;
import CatBank.Security.Service.RoleService;
import CatBank.Security.Service.UserService;
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
//@RequestMapping("/accountHolder")
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


    @PreAuthorize("hasRole('ACCOUNTHOLDER')")
    @GetMapping("/checking/balance/{checkingId}")
    public BigDecimal getBalance(@PathVariable(value = "checkingId") int checkingId){
        return checkingService.allFeeApplycations(checkingId);
}

    @PreAuthorize("hasRole('ACCOUNTHOLDER')")
    @PostMapping("checking/transferenceBetweenCheckings/")
    public Object transferMoneyBetweenCheckings(@Valid @RequestBody TransferenceDTO transferenceDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new MensajeDTO("Los campos introducidos son incorrectos"), HttpStatus.BAD_REQUEST);
        }
        return checkingService.transferMoneyBetweenCheckings(transferenceDTO);
    }




}
