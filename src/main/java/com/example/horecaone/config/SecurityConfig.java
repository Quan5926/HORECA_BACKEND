package com.example.horecaone.config;

import com.example.horecaone.security.login.*;
import com.example.horecaone.security.login.MD5PasswordEncoder;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter    jwtAuthFilter;
    private final CustomOAuth2UserService    customOAuth2UserService;
    private final OAuth2LoginSuccessHandler  oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler  oAuth2LoginFailureHandler; // ← thêm

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                          CustomOAuth2UserService customOAuth2UserService,
                          OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
                          OAuth2LoginFailureHandler oAuth2LoginFailureHandler) { // ← thêm
        this.jwtAuthFilter              = jwtAuthFilter;
        this.customOAuth2UserService    = customOAuth2UserService;
        this.oAuth2LoginSuccessHandler  = oAuth2LoginSuccessHandler;
        this.oAuth2LoginFailureHandler  = oAuth2LoginFailureHandler; // ← thêm
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("{\"error\": \"Unauthorized\"}");
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/login/**", "/oauth2/**", "/error").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/api/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/api/v1/user/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/api/categories/**", "/api/public/categories").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/api/news/**", "/api/news").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/api/reviews/news/**").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/api/banners/public").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/api/khuyen-mai-flash/**").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/api/reviews/**").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/api/reviews/product/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/reviews/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/lien-he").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/api/thong-tin-cong-ty").permitAll()
                        .requestMatchers("/api/v1/cart/**").permitAll()
                        .requestMatchers("/api/v1/order/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/*/set-password").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/me/set-password").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/**").authenticated()
                        .requestMatchers("/api/v1/addresses/**").authenticated()
                        .requestMatchers(HttpMethod.POST,   "/api/products/**", "/api/upload/**")
                        .hasAnyAuthority("QUAN_TRI", "ROLE_QUAN_TRI")
                        .requestMatchers(HttpMethod.PUT,    "/api/products/**")
                        .hasAnyAuthority("QUAN_TRI", "ROLE_QUAN_TRI")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**")
                        .hasAnyAuthority("QUAN_TRI", "ROLE_QUAN_TRI")
                        .requestMatchers("/api/banners/**").hasAnyAuthority("QUAN_TRI", "ROLE_QUAN_TRI")
                        .requestMatchers("/api/admin/**").hasAnyAuthority("QUAN_TRI", "ROLE_QUAN_TRI")
                        .anyRequest().authenticated()
                )

                // ← Thay lambda cũ bằng handler riêng
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo ->
                                userInfo.oidcUserService(customOAuth2UserService))
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler) // ← gọn, rõ ràng
                )

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return new MD5PasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean

    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:5173",
                "http://localhost:5174",
                "http://localhost:4200",
                "http://horecavietnam.com",       // ← THÊM
                "http://www.horecavietnam.com"
        ));
        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}