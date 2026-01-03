package sk.mvp.user_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private JwtUtil jwtUtil;
    private CustomUserDetailsService customUserDetailsService;

    public JwtAuthFilter(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {

          String jwt = parseJwt(request);
          if (jwt != null && jwtUtil.validateToken(jwt)) {
               String username = jwtUtil.getUsernameFromToken(jwt);
               UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
              UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                      userDetails,
                      null,
                      userDetails.getAuthorities());
              authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
              SecurityContextHolder.getContext().setAuthentication(authentication);

          }
        } catch (Exception e) {
            System.out.println("Cannot set user authentication: " + e.getMessage());
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
