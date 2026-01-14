package sk.mvp.user_service.integration;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.token.TokenService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import sk.mvp.user_service.TestContainerConfig;
import sk.mvp.user_service.auth.service.impl.TokenServiceImpl;
import sk.mvp.user_service.common.config.JwtConfig;
import sk.mvp.user_service.common.exception.data.ErrorType;
import sk.mvp.user_service.common.utils.JwtUtil;
import sk.mvp.user_service.entity.User;
import sk.mvp.user_service.user.dto.ContactResp;
import sk.mvp.user_service.user.dto.UserProfile;
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
    private User user;

//    @Autowired
//    private TokenServiceImpl tokenService;
//    @Autowired
//    private JwtConfig jwtConfig;
//    @Autowired
//    private UserServiceImpl userService;



    private String accesToken;


    // TODO: najskor jednoduchy potom parmetrizovany, potom unit testy
    @Transactional
    @ParameterizedTest
    @ValueSource(strings = {"turb","mkovac"})
    void shouldReturnUserProfileFromCookie(String username) throws Exception {
        User user = userRepository.findByUsername(username).get();
        String accesToken = jwtUtil.generateAccessToken(user.getUsername(),
                user.getTokenVersion(),
                UUID.randomUUID().toString(),
                user.getRolesAsString());
        Cookie cookie = new Cookie("access_token", accesToken);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/profile/get").cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(user.getLastName()))
                .andExpect(jsonPath("$.genderCode").value(user.getGender().getCode()+""))
                .andExpect(jsonPath("$.contact.email").value(user.getContact().getEmail()))
                .andExpect(jsonPath("$.contact.phoneNumber").value(user.getContact().getPhoneNumber()));


    }

    //change profile data test
    @Test
    @Transactional
    void shouldChangeUserProfileData() throws Exception {
        User user = userRepository.findByUsername("turb").get();
        String accesToken = jwtUtil.generateAccessToken(user.getUsername(),
                user.getTokenVersion(),
                UUID.randomUUID().toString(),
                user.getRolesAsString());
        Cookie cookie = new Cookie("access_token", accesToken);
        //compose request
        UserProfile req = new UserProfile();
        req.setFirstName("Antonin");
        req.setLastName("Novotny");
        // DTO to JSON (Jackson ObjectMapper)
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(req);

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/api/profile/update")
                                .cookie(cookie)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody))
                .andExpect(status().isOk());
        //fetch updated user from db and comapres values from request

        User updatedUser = userRepository.findByUsername("turb").get();
        Assertions.assertEquals(req.getFirstName(), updatedUser.getFirstName());
        Assertions.assertEquals(req.getLastName(), updatedUser.getLastName());
    }

    @Test
    @Transactional
    void souldFailChangeUserPorfileDataWithExistingEmail() throws Exception {
        User user = userRepository.findByUsername("turb").get();
        String accesToken = jwtUtil.generateAccessToken(user.getUsername(),
                user.getTokenVersion(),
                UUID.randomUUID().toString(),
                user.getRolesAsString());
        Cookie cookie = new Cookie("access_token", accesToken);
        //compose request with exiting email
        UserProfile req = new UserProfile();
        ContactResp contactResp = new ContactResp("filip.toth@example.com", "+421917854789");
        req.setContact(contactResp);
        // DTO to JSON (Jackson ObjectMapper)
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(req);

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/api/profile/update")
                                .cookie(cookie)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.errorType").value(ErrorType.EMAIL_DUPLICATED.toString()));

    }

}
