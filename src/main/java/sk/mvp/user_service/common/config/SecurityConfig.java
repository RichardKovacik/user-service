package sk.mvp.user_service.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import sk.mvp.user_service.auth.service.impl.QUserDetailsService;
import sk.mvp.user_service.common.filter.security.JwtAuthFilter;
import sk.mvp.user_service.common.filter.security.QUsernamePasswordAuthFilter;
import sk.mvp.user_service.common.reddis.IRedisService;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private QUserDetailsService qUserDetailsService;
    private JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(QUserDetailsService qUserDetailsService,
                          JwtAuthFilter jwtAuthFilter) {
        this.qUserDetailsService = qUserDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, QUsernamePasswordAuthFilter qUsernamePasswordAuthFilter) throws Exception {
        http

                // csfr simple config
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF
                .cors(AbstractHttpConfigurer::disable) // Disable CORS (or configure if needed)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/web/login",
                                "/api/auth/registration",
                                "/api/auth/refresh").permitAll()
                        .requestMatchers("/api/auth/logout").authenticated()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/profile/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
//                .logout(logout -> logout
//                        .logoutUrl("/api/users/logout")
////                        .invalidateHttpSession(true)
////                        .clearAuthentication(true)
////                        .deleteCookies("JSESSIONID")
//                )
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
        // Add the JWT Token filter before the UsernamePasswordAuthenticationFilter
                http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
                http.addFilterAt(qUsernamePasswordAuthFilter, UsernamePasswordAuthenticationFilter.class);
                http.redirectToHttps(withDefaults());
        return http.build();
    }
    @Bean
    public QUsernamePasswordAuthFilter qUsernamePasswordAuthFilter(
            AuthenticationManager authenticationManager,
            AuthenticationFailureHandler successHandler,
            AuthenticationSuccessHandler failureHandler,
            ObjectMapper objectMapper,
            IRedisService redisService) {
        return new QUsernamePasswordAuthFilter(authenticationManager, failureHandler, successHandler, objectMapper, redisService
        );
    }
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

}
