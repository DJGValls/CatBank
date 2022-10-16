package CatBank.Controller;

import CatBank.Model.Checking;
import CatBank.Model.User.AccountHolder;
import CatBank.Model.User.AccountHolderAddress;
import CatBank.Model.User.ThirdParty;
import CatBank.Repository.AccountHolderRepository;
import CatBank.Repository.CheckingRepository;
import CatBank.Repository.ThirdPartyRepository;
import CatBank.Security.JasonWebToken.JwtProvider;
import CatBank.Security.Model.Enums.RoleName;
import CatBank.Security.Model.Role;
import CatBank.Security.Model.User;
import CatBank.Security.Repository.UserRepository;
import CatBank.Security.Service.RoleService;
import CatBank.Service.AccountHolderService;
import CatBank.Utils.Money;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static CatBank.Model.Enums.Status.ACTIVE;
import static CatBank.Utils.Money.USD;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class CreditCardControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    AccountHolderRepository accountHolderRepository;
    @Autowired
    ThirdPartyRepository thirdPartyRepository;
    @Autowired
    CheckingRepository checkingRepository;
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    RoleService roleService;
    @Autowired
    AccountHolderService accountHolderService;
    private MockMvc mockMvc;
    private String Value;
    private User adminUser1;
    private final ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new ParameterNamesModule())
            .addModule(new Jdk8Module())
            .addModule(new JavaTimeModule())
            .build();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        adminUser1 = new User("admin", passwordEncoder.encode("1234"));
        Set<Role> roles = new HashSet<>();
        roles.add((roleService.getByRoleName(RoleName.ROLE_ADMIN).get()));
        roles.add((roleService.getByRoleName(RoleName.ROLE_USERTHIRDPARTY).get()));
        roles.add((roleService.getByRoleName(RoleName.ROLE_ACCOUNTHOLDER).get()));
        adminUser1.setRoles(roles);
        userRepository.saveAll(List.of(adminUser1));
        String noEncodePassword = "1234";
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(adminUser1.getUserName(),
                        noEncodePassword));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateToken(authentication);
        Value = "Bearer " + jwt;
        AccountHolder accountHolder1 = new AccountHolder("Superintendente", passwordEncoder.encode("1234"), LocalDate.of(1980,01,01), new AccountHolderAddress("13 rue del percebe"), "supervicente@gmail.com" );
        AccountHolder accountHolder2 = new AccountHolder("Bacterio", passwordEncoder.encode("1234"), LocalDate.of(1980,01,01), new AccountHolderAddress("13 rue del percebe"), "bacterio@gmail.com" );
        accountHolderRepository.saveAll(List.of(accountHolder1, accountHolder2));
        ThirdParty thirdParty1 = new ThirdParty("Filemon", passwordEncoder.encode("1234"));
        ThirdParty thirdParty2 = new ThirdParty("Bestiajez", passwordEncoder.encode("1234"));
        thirdPartyRepository.saveAll(List.of(thirdParty1, thirdParty2));
        Checking checking1 = new Checking("Superintendente","Ofelia",new Money(BigDecimal.valueOf(10000), USD),1234, ACTIVE, accountHolderService.findByEmail("supervicente@gmail.com").get());
        Checking checking2 = new Checking("Filemon", "Ofelia", new Money(BigDecimal.valueOf(10000), USD), 1234, thirdPartyRepository.findByUserName("Filemon").get());
        checkingRepository.saveAll(List.of(checking1, checking2));
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createCreditCard() {
    }

    @Test
    void getBalance() {
    }

    @Test
    void creditCardTransferMoney() {
    }

    @Test
    void udateCreditCard() {
    }

    @Test
    void getCreditCard() {
    }

    @Test
    void updateDates() {
    }

    @Test
    void creditCardsList() {
    }
}