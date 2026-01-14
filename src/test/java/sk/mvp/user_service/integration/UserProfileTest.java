package sk.mvp.user_service.integration;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.token.TokenService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import sk.mvp.user_service.TestContainerConfig;
import sk.mvp.user_service.auth.service.impl.TokenServiceImpl;
import sk.mvp.user_service.common.config.JwtConfig;
import sk.mvp.user_service.common.utils.JwtUtil;
import sk.mvp.user_service.entity.User;
import sk.mvp.user_service.user.repository.UserRepository;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import sk.mvp.user_service.user.service.UserServiceImpl;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(TestContainerConfig.class)
@ActiveProfiles("test")
@Sql(scripts = "/data-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@AutoConfigureMockMvc(addFilters = false)
public class UserProfileTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;

//    @Autowired
//    private TokenServiceImpl tokenService;
//    @Autowired
//    private JwtConfig jwtConfig;
//    @Autowired
//    private UserServiceImpl userService;



    private String accesToken;


    // TODO: najskor jednoduchy potom parmetrizovany, potom unit testy
    @Test
    @Transactional
    void shouldReturnUserProfileFromCookie() throws Exception {
        User user = userRepository.findByUsername("turb").get();
        String accesToken = jwtUtil.generateAccessToken(user.getUsername(),
                user.getTokenVersion(),
                UUID.randomUUID().toString(),
                user.getRolesAsString());
        Cookie cookie = new Cookie("access_token", accesToken);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/profile/get").cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()));


    }

}
