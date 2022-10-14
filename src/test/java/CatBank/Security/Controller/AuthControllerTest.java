package CatBank.Security.Controller;

import CatBank.Security.DTO.JwtDTO;
import CatBank.Security.JasonWebToken.JwtProvider;
import CatBank.Security.Model.Role;
import CatBank.Security.Model.Enums.RoleName;
import CatBank.Security.Model.User;
import CatBank.Security.Repository.RoleRepository;
import CatBank.Security.Repository.UserRepository;
import CatBank.Security.Service.RoleService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.http.parser.Authorization;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static CatBank.Security.Model.Enums.RoleName.ROLE_ACCOUNTHOLDER;
import static org.junit.jupiter.api.Assertions.*;
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
    JwtProvider jwtProvider;
    @Autowired
    RoleService roleService;



    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String Value;
    private User adminUser1;

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
    @DisplayName("List of users")
    void listAdmins() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/auth/adminList").header("Authorization", Value))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString().contains("admin"));
        List<User> users = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<User>>() {});
                assertEquals(1, users.size());
    }
}