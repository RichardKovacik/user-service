package sk.mvp.user_service.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import sk.mvp.user_service.dto.jwt.TokenPair;
import sk.mvp.user_service.dto.user.UserLoginReqDTO;

public interface IAuthService {
    void revokeTokens(String username);
    TokenPair loginUser(UserLoginReqDTO userLoginReqDTO, HttpServletRequest request);
}
