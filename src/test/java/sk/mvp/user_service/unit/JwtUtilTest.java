package sk.mvp.user_service.unit;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import sk.mvp.user_service.common.config.JwtConfig;
import sk.mvp.user_service.common.utils.JwtUtil;

import java.util.UUID;

@SpringJUnitConfig(
        classes = JwtConfig.class,
        initializers = ConfigDataApplicationContextInitializer.class
)
@ActiveProfiles("test")
public class JwtUtilTest {
    @Autowired
    private JwtConfig jwtConfig;
    private int tokenVersion = 1;
    private String[] roles;
    private  String accesTokenId= UUID.randomUUID().toString();

    @BeforeEach
    public void setUp(){
        tokenVersion = 1;
        roles = new String[1];
        roles[0] = "ROLE_USER";
    }

    @ParameterizedTest
    @ValueSource(strings = {"marek","ferko"})
    public void testGenerateAccessToken(String username) throws Exception{
        String accessToken = JwtUtil.generateAccessToken(username,
                tokenVersion, accesTokenId, roles, jwtConfig.getAccesKey(), jwtConfig.getAccesTokenExpiration());
        Claims claims = JwtUtil.parseClaimsFromJwtToken(accessToken, jwtConfig.getAccesKey());

        Assertions.assertEquals(username, claims.getSubject());
        Assertions.assertEquals(accesTokenId, claims.getId());
        Assertions.assertEquals("access_token", claims.get("type", String.class));



    }
}
