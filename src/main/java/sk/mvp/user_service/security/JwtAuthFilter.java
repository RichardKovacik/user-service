package sk.mvp.user_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sk.mvp.user_service.service.jwt.ITokenService;

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
            String jwt = parseJwt(request);

            if (jwt != null) {
                // parse jet token a get user detail obejct from it
                UserDetails userDetails = jwtService.getUserDetailFromAccessToken(jwt);
                jwtService.validateAccessToken(jwt, userDetails);

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
