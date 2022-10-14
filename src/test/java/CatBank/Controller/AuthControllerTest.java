package CatBank.Controller;

import CatBank.Model.User.AccountHolder;
import CatBank.Model.User.AccountHolderAddress;
import CatBank.Repository.AccountHolderRepository;
import CatBank.Security.JasonWebToken.JwtProvider;
import CatBank.Security.Model.Enums.RoleName;
import CatBank.Security.Model.Role;
import CatBank.Security.Model.User;
import CatBank.Security.Repository.RoleRepository;
import CatBank.Security.Repository.UserRepository;
import CatBank.Security.Service.RoleService;
import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
class AuthControllerTest {
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
    JwtProvider jwtProvider;
    @Autowired
    RoleService roleService;
    private MockMvc mockMvc;
    private String Value;
    private User adminUser1;
    private final ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new ParameterNamesModule())
            .addModule(new Jdk8Module())
            .addModule(new JavaTimeModule())
            .build();



    @BeforeEach
    void setUp() throws Exception {
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
    }
    @AfterEach
    void tearDown() {
        roleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Create ThirdParty User")
    void newUserThirdParty() throws Exception {
        User user1 = new User("Mortadelo", passwordEncoder.encode("1234"));
        Set<Role> roles = new HashSet<>();
        roles.add((roleService.getByRoleName(RoleName.ROLE_USERTHIRDPARTY).get()));
        user1.setRoles(roles);
        String payload = objectMapper.writeValueAsString(user1);
        MvcResult mvcResult = mockMvc.perform(post("/auth/newUserThirdParty").header("Authorization", Value)
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        User user = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), User.class);
        assertEquals(user1.getUserName(), user.getUserName());
    }

    @Test
    @DisplayName("create Account Holder")
    void newUserAccountHolder() throws Exception {

        AccountHolder accountHolder1 = new AccountHolder("Mortadelo", passwordEncoder.encode("1234"), LocalDate.of(1980,01,01), new AccountHolderAddress("13 rue del percebe"), "maginelmago@gmail.com" );
        String payload = objectMapper.writeValueAsString(accountHolder1);
        MvcResult mvcResult = mockMvc.perform(post("/auth/newUserAccountHolder").header("Authorization", Value)
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("Mortadelo"));
    }

    @Test
    @DisplayName("List of Admins")
    void listAdmins() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/auth/adminList").header("Authorization", Value))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("admin"));
        List<User> users = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<User>>() {});
        assertEquals(1, users.size());
    }

    @Test
    @DisplayName(("List of ThirdParty"))
    void listUsers() throws Exception {
        User user1 = new User("Filemon", passwordEncoder.encode("1234"));
        Set<Role> roles = new HashSet<>();
        roles.add((roleService.getByRoleName(RoleName.ROLE_USERTHIRDPARTY).get()));
        user1.setRoles(roles);
        userRepository.saveAll(List.of(user1));
        MvcResult mvcResult = mockMvc.perform(get("/auth/userThirdPartyList").header("Authorization", Value))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        List<User> users = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<User>>() {});
        assertTrue(mvcResult.getResponse().getContentAsString().contains("Filemon"));
        assertEquals(1, users.size());
    }


    @Test
    @DisplayName(("List of accountholder"))
    void accountHoldersList() throws Exception {
        AccountHolder accountHolder1 = new AccountHolder("Superintendente", passwordEncoder.encode("1234"), LocalDate.of(1980,01,01), new AccountHolderAddress("13 rue del percebe"), "maginelmago@gmail.com" );

        accountHolderRepository.save(accountHolder1);
        MvcResult mvcResult = mockMvc.perform(get("/auth/accountHolderList").header("Authorization", Value))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        List<AccountHolder> users = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<AccountHolder>>() {});
        assertTrue(mvcResult.getResponse().getContentAsString().contains("Superintendente"));
        assertEquals(1, users.size());
    }


}