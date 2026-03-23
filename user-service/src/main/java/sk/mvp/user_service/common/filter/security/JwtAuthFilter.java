package sk.mvp.user_service.common.filter.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;
import sk.mvp.user_service.auth.service.ITokenService;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private ITokenService jwtService;
    private static final Logger log =
            LoggerFactory.getLogger(JwtAuthFilter.class);

    public JwtAuthFilter(ITokenService jwtService) {
       this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            //auth by header
            String accessToken = parseJwt(request);

            //auth by cookie from web apps
            // auth by cookie
            if (accessToken == null) {
                Cookie cookie = WebUtils.getCookie(request, "access_token");
                if (cookie != null && cookie.getValue() != null) accessToken = cookie.getValue();
            }

            if (accessToken != null) {
                // parse jet token a get user detail obejct from it
                UserDetails userDetails = jwtService.getUserDetailFromAccessToken(accessToken);
                jwtService.validateAccessToken(accessToken, userDetails);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);

            }
        } catch (Exception e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);

    }

    private String parseJwt(HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        if(jwt != null && jwt.startsWith("Bearer ")){
            return jwt.substring(7);
        }
       return null;
    }



}
