package CatBank.Controller;

import CatBank.Model.Checking;
import CatBank.Model.DTO.FactoryAccountDTO;
import CatBank.Model.DTO.MoneyDTO;
import CatBank.Model.DTO.TransferenceDTO;
import CatBank.Model.Enums.AccountType;
import CatBank.Model.Enums.Status;
import CatBank.Model.Savings;
import CatBank.Model.StudentChecking;
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

import static CatBank.Utils.Money.USD;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class StudentCheckingControllerTest {
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
    StudentCheckingRepository studentCheckingRepository;
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
        AccountHolder accountHolder1 = new AccountHolder("Superintendente", passwordEncoder.encode("1234"), LocalDate.of(2010,01,01), new AccountHolderAddress("13 rue del percebe"), "supervicente@gmail.com" );
        AccountHolder accountHolder2 = new AccountHolder("Bacterio", passwordEncoder.encode("1234"), LocalDate.of(1980,01,01), new AccountHolderAddress("13 rue del percebe"), "bacterio@gmail.com" );
        accountHolderRepository.saveAll(List.of(accountHolder1, accountHolder2));
        ThirdParty thirdParty1 = new ThirdParty("Filemon", passwordEncoder.encode("1234"));
        ThirdParty thirdParty2 = new ThirdParty("Bestiajez", passwordEncoder.encode("1234"));
        thirdPartyRepository.saveAll(List.of(thirdParty1, thirdParty2));
        StudentChecking studentChecking1 = new StudentChecking("Superintendente", "Ofelia", new Money(BigDecimal.valueOf(10000), USD), 1234, Status.ACTIVE,accountHolder1);
        studentCheckingRepository.saveAll(List.of(studentChecking1));
        Checking checking1 = new Checking("Filemon", "Ofelia", new Money(BigDecimal.valueOf(10000), USD), 1234, thirdPartyRepository.findByUserName("Filemon").get());
        checkingRepository.saveAll(List.of(checking1));
    }

    @AfterEach
    void tearDown() {
        studentCheckingRepository.deleteAll();
        userRepository.deleteAll();
        accountHolderRepository.deleteAll();
        thirdPartyRepository.deleteAll();
    }

    @Test
    void studentCheckingTransferMoney() throws Exception {
        TransferenceDTO transferenceDTO1 = new TransferenceDTO(1,"Superintendente",1234, BigDecimal.valueOf(1000), 1, "Filemon", AccountType.CHECKING);
        String payload = objectMapper.writeValueAsString(transferenceDTO1);
        MvcResult mvcResult = mockMvc.perform(post("/studentChecking/transferMoney/").header("Authorization", Value)
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("9000"));
    }

    @Test
    void udateChecking() throws Exception {
        FactoryAccountDTO factoryAccountDTO = new FactoryAccountDTO(new MoneyDTO("USD", BigDecimal.valueOf(-3000)));
        String payload = objectMapper.writeValueAsString(factoryAccountDTO);
        MvcResult mvcResult = mockMvc.perform(patch("/studentChecking/updateStudentCheckingBalance/1")
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("7000"));
    }

    @Test
    void getStudentChecking() throws Exception {
        AccountHolder accountHolder = new AccountHolder(1, "Superintendente", "supervicente@gmail.com");
        String payload = objectMapper.writeValueAsString(accountHolder);
        MvcResult mvcResult = mockMvc.perform(post("/studentChecking/studentCheckingInfo")
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("Superintendente"));
    }

    @Test
    void studentCheckingsList() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/studentChecking/studentCheckingList").header("Authorization", Value))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        List<StudentChecking> studentCheckingList = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<StudentChecking>>() {});
        assertTrue(mvcResult.getResponse().getContentAsString().contains("Superintendente"));
        assertEquals(1, studentCheckingList.size());
    }
}