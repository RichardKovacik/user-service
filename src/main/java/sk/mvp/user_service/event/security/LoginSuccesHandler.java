package sk.mvp.user_service.event.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import sk.mvp.user_service.common.constants.AuthConts;
import sk.mvp.user_service.common.reddis.IRedisService;

import javax.imageio.event.IIOReadProgressListener;
import java.io.IOException;

@Component
public class LoginSuccesHandler implements AuthenticationSuccessHandler {
    private IRedisService redisService;

    public LoginSuccesHandler(IRedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String username = (String) request.getAttribute("username");
        String loginAttemptsKey = AuthConts.REDISS_AUTH_LOGIN_ATTEMPTS_USER_COLL + username;
        // delete attemts counter in reddis
        redisService.delete(loginAttemptsKey);

    }
}
