package CatBank.Controller;

import CatBank.Model.Checking;
import CatBank.Model.DTO.*;
import CatBank.Model.Enums.AccountType;
import CatBank.Model.Enums.Status;
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
import CatBank.Security.Repository.RoleRepository;
import CatBank.Security.Repository.UserRepository;
import CatBank.Security.Service.RoleService;
import CatBank.Service.AccountHolderService;
import CatBank.Utils.Money;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static CatBank.Model.Enums.Status.ACTIVE;
import static CatBank.Utils.Money.USD;
import static java.lang.Enum.valueOf;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class CheckingControllerTest {


    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private RoleRepository roleRepository;
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
        accountHolderRepository.save(accountHolder1);
        ThirdParty thirdParty1 = new ThirdParty("Filemon", passwordEncoder.encode("1234"));
        thirdPartyRepository.saveAll(List.of(thirdParty1));
    }

    @AfterEach
    void tearDown() {
        checkingRepository.deleteAll();
        userRepository.deleteAll();
        accountHolderRepository.deleteAll();
        thirdPartyRepository.deleteAll();
    }
    @Test
    @DisplayName("create Checking for Account Holder")
    void createChecking() throws Exception {
        FactoryAccountDTO checking1 = new FactoryAccountDTO("Superintendente","Ofelia",new MoneyDTO("USD", BigDecimal.valueOf(10000)),1234, ACTIVE, accountHolderService.findByEmail("supervicente@gmail.com").get());
        String payload = objectMapper.writeValueAsString(checking1);
        MvcResult mvcResult = mockMvc.perform(post("/checking/accountHolder/createChecking").header("Authorization", Value)
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("Superintendente"));
    }

    @Test
    @DisplayName("create Checking for Third Party")
    void createCheckingThirdParty() throws Exception {
        ThirdPartyFactoryAccountDTO checking1 = new ThirdPartyFactoryAccountDTO("Filemon", "Ofelia", new MoneyDTO("USD", BigDecimal.valueOf(10000)), 1234, thirdPartyRepository.findByUserName("Filemon").get());
        String payload = objectMapper.writeValueAsString(checking1);
        MvcResult mvcResult = mockMvc.perform(post("/checking/thirdParty/createChecking").header("Authorization", Value)
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("Filemon"));
    }

    @Test
    @DisplayName("Transfer money from checking")
    void transferMoneyChecking() throws Exception {
        Checking checking1 = new Checking("Superintendente","Ofelia",new Money(BigDecimal.valueOf(10000), USD),1234, ACTIVE, accountHolderService.findByEmail("supervicente@gmail.com").get());
        Checking checking2 = new Checking("Filemon", "Ofelia", new Money(BigDecimal.valueOf(10000), USD), 1234, thirdPartyRepository.findByUserName("Filemon").get());
        checkingRepository.saveAll(List.of(checking1, checking2));
        TransferenceDTO transferenceDTO1 = new TransferenceDTO(1,"Superintendente",1234, BigDecimal.valueOf(1000), 2, "Filemon", AccountType.CHECKING);
        String payload = objectMapper.writeValueAsString(transferenceDTO1);
        MvcResult mvcResult = mockMvc.perform(post("/checking/transferMoney/").header("Authorization", Value)
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("9000"));
    }

    @Test
    @DisplayName("Update balnce of checking account")
    void udateChecking() throws Exception {
        Checking checking1 = new Checking("Superintendente","Ofelia",new Money(BigDecimal.valueOf(10000), USD),1234, ACTIVE, accountHolderService.findByEmail("supervicente@gmail.com").get());
        Checking checking2 = new Checking("Filemon", "Ofelia", new Money(BigDecimal.valueOf(10000), USD), 1234, thirdPartyRepository.findByUserName("Filemon").get());
        checkingRepository.saveAll(List.of(checking1, checking2));
        FactoryAccountDTO factoryAccountDTO = new FactoryAccountDTO(new MoneyDTO("USD", BigDecimal.valueOf(-3000)));
        String payload = objectMapper.writeValueAsString(factoryAccountDTO);
        MvcResult mvcResult = mockMvc.perform(patch("/checking/updateCheckingBalance/1")
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("7000"));
    }

    @Test
    void updateDates() throws Exception {
        Checking checking1 = new Checking("Superintendente","Ofelia",new Money(BigDecimal.valueOf(10000), USD),1234, ACTIVE, accountHolderService.findByEmail("supervicente@gmail.com").get());
        Checking checking2 = new Checking("Filemon", "Ofelia", new Money(BigDecimal.valueOf(10000), USD), 1234, thirdPartyRepository.findByUserName("Filemon").get());
        checkingRepository.saveAll(List.of(checking1, checking2));
        UpadteDatesDTO upadteDatesDTO = new UpadteDatesDTO( LocalDate.of(2021,01,01), LocalDate.of(2021,02,02));
        String payload = objectMapper.writeValueAsString(upadteDatesDTO);
        MvcResult mvcResult = mockMvc.perform(patch("/checking/updateDates/1")
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("2021-01-01"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains("2021-02-02"));
    }

    @Test
    void getChecking() {
    }

    @Test
    void checkingsList() {
    }
}