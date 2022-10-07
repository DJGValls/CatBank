package CatBank.Controller;

import CatBank.Security.JasonWebToken.JwtProvider;
import CatBank.Security.Service.RoleService;
import CatBank.Security.Service.UserService;
import CatBank.Service.CheckingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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


@PreAuthorize("hasRole('ACCOUNTHOLDER')")
@GetMapping("/checking/balance/{checkingId}")
    public void getBalance(@PathVariable(value = "checkingId") int checkingId){
    checkingService.feeApplycations(checkingId);
}

}
