package sk.mvp.user_service.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import sk.mvp.user_service.dto.jwt.TokenPair;
import sk.mvp.user_service.dto.user.UserLoginReqDTO;
import sk.mvp.user_service.service.jwt.ITokenService;

@Service
public class AuthServiceImpl implements IAuthService {
    private ITokenService jwtService;
    private AuthenticationManager authenticationManager;

    public AuthServiceImpl(ITokenService jwtService, AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void revokeTokens(String username) {
        jwtService.revokeAllTokens(username);
    }

    @Override
    public TokenPair loginUser(UserLoginReqDTO userLoginReqDTO, HttpServletRequest request) {
        //prebhene autetifikacia najdenie usera, provnanei hesla
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLoginReqDTO.username(),
                        userLoginReqDTO.password()
                )
        );
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        return jwtService.generateTokenPair(userDetails);
    }
}
