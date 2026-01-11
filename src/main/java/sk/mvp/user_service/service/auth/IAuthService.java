package sk.mvp.user_service.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import sk.mvp.user_service.dto.jwt.TokenPair;
import sk.mvp.user_service.dto.user.UserLoginReqDTO;
import sk.mvp.user_service.exception.ApplicationException;
import sk.mvp.user_service.exception.InvalidTokenException;

public interface IAuthService {
    void revokeTokens(String username);
    TokenPair loginUser(UserLoginReqDTO userLoginReqDTO);
    TokenPair refreshTokens(String refreshToken) throws ApplicationException;
    // void logout(HttpServletRequest request);
}
