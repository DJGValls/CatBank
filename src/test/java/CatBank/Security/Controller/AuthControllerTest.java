package CatBank.Security.Controller;

import CatBank.Security.Model.Role;
import CatBank.Security.Model.RoleName;
import CatBank.Security.Model.User;
import CatBank.Security.Repository.RoleRepository;
import CatBank.Security.Repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();



    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Role roleAdmin = new Role(RoleName.ROLE_ADMIN);
        Role roleUser = new Role(RoleName.ROLE_USER);
        User user1 = new User("Silvester","piolin");
        User user2 = new User("Tom","jerry");
        User adminUser1 = new User("admin_Isidoro", "isi");
        User adminUser2 = new User("Garfield_admin", "lasaña");
        roleRepository.saveAll(List.of(roleAdmin, roleUser));
        userRepository.saveAll(List.of(user1, user2, adminUser1, adminUser2));

    }

    @AfterEach
    void tearDown() {

        roleRepository.deleteAll();
        userRepository.deleteAll();

    }

    @Test
    @DisplayName("List of users")
    void newUser() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/auth/adminList"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString().contains("admin_Isidoro"));
        List<User> users = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<User>>() {});
                assertEquals(2, users.size());
    }

    @Test
    void login() {
    }

    @Test
    void testUser() {
    }

    @Test
    void testAdmin() {
    }
}