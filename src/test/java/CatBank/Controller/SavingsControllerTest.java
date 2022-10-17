package CatBank.Controller;

import CatBank.Model.Checking;
import CatBank.Model.CreditCard;
import CatBank.Model.DTO.FactoryAccountDTO;
import CatBank.Model.DTO.MoneyDTO;
import CatBank.Model.DTO.TransferenceDTO;
import CatBank.Model.DTO.UpadteDatesDTO;
import CatBank.Model.Enums.AccountType;
import CatBank.Model.Enums.Status;
import CatBank.Model.Savings;
import CatBank.Model.User.AccountHolder;
import CatBank.Model.User.AccountHolderAddress;
import CatBank.Model.User.ThirdParty;
import CatBank.Repository.*;
import CatBank.Security.JasonWebToken.JwtProvider;
import CatBank.Security.Model.Enums.RoleName;
import CatBank.Security.Model.Role;
import CatBank.Security.Model.User;
import CatBank.Security.Repository.UserRepository;
import CatBank.Security.Service.RoleService;
import CatBank.Service.AccountHolderService;
import CatBank.Utils.Money;
import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class SavingsControllerTest {

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
    SavingsRepository savingsRepository;
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    RoleService roleService;
    @Autowired
    AccountHolderService accountHolderService;
    @Autowired
    CheckingRepository checkingRepository;
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
        Savings savings1 = new Savings("Superintendente", "Ofelia", new Money(BigDecimal.valueOf(10000), USD), 1234, Status.ACTIVE,accountHolder1);
        savingsRepository.saveAll(List.of(savings1));
        Checking checking1 = new Checking("Filemon", "Ofelia", new Money(BigDecimal.valueOf(10000), USD), 1234, thirdPartyRepository.findByUserName("Filemon").get());
        checkingRepository.saveAll(List.of(checking1));
    }

    @AfterEach
    void tearDown() {
        savingsRepository.deleteAll();
        userRepository.deleteAll();
        accountHolderRepository.deleteAll();
        thirdPartyRepository.deleteAll();
    }

    @Test
    void createSavings() throws Exception {
        FactoryAccountDTO checking1 = new FactoryAccountDTO("Bacterio","Ofelia",new MoneyDTO("USD", BigDecimal.valueOf(10000)),1234, ACTIVE, accountHolderService.findByEmail("bacterio@gmail.com").get());
        String payload = objectMapper.writeValueAsString(checking1);
        MvcResult mvcResult = mockMvc.perform(post("/savings/createSavings").header("Authorization", Value)
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("Bacterio"));
    }

    @Test
    void savingsTransferMoney() throws Exception {
        TransferenceDTO transferenceDTO1 = new TransferenceDTO(1,"Superintendente",1234, BigDecimal.valueOf(1000), 1, "Filemon", AccountType.CHECKING);
        String payload = objectMapper.writeValueAsString(transferenceDTO1);
        MvcResult mvcResult = mockMvc.perform(post("/savings/transferMoney/").header("Authorization", Value)
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("9000"));
    }

    @Test
    void udateSavings() throws Exception {
        FactoryAccountDTO factoryAccountDTO = new FactoryAccountDTO(new MoneyDTO("USD", BigDecimal.valueOf(-3000)));
        String payload = objectMapper.writeValueAsString(factoryAccountDTO);
        MvcResult mvcResult = mockMvc.perform(patch("/savings/updateSavingsBalance/1")
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("7000"));
    }

    @Test
    void getSavings() throws Exception {
        AccountHolder accountHolder = new AccountHolder(1, "Superintendente", "supervicente@gmail.com");
        String payload = objectMapper.writeValueAsString(accountHolder);
        MvcResult mvcResult = mockMvc.perform(post("/savings/savingsInfo")
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("Superintendente"));
    }

    @Test
    void updateDates() throws Exception {
        UpadteDatesDTO upadteDatesDTO = new UpadteDatesDTO( LocalDate.of(2021,01,01), LocalDate.of(2021,02,02));
        String payload = objectMapper.writeValueAsString(upadteDatesDTO);
        MvcResult mvcResult = mockMvc.perform(patch("/savings/updateDates/1")
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("2021-01-01"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains("2021-02-02"));
    }

    @Test
    void savingsList() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/savings/savingsList").header("Authorization", Value))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        List<Savings> savingsList = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<Savings>>() {});
        assertTrue(mvcResult.getResponse().getContentAsString().contains("Superintendente"));
        assertEquals(1, savingsList.size());
    }
}