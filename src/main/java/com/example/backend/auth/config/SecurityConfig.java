package com.example.backend.auth.config;

import com.example.backend.auth.handler.OAuth2SuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final OAuth2SuccessHandler successHandler;
    private final RateLimitFilter rateLimitFilter;

    public SecurityConfig(JwtFilter jwtFilter,
                          OAuth2SuccessHandler successHandler,
                          RateLimitFilter rateLimitFilter) {
        this.jwtFilter = jwtFilter;
        this.successHandler = successHandler;
        this.rateLimitFilter = rateLimitFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // ❌ disable csrf (JWT based)
                .csrf(AbstractHttpConfigurer::disable)

                // 🔥 stateless (no session)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 🌐 CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 🔐 headers
                .headers(headers -> headers
                        .contentSecurityPolicy(csp ->
                                csp.policyDirectives(
                                        "default-src 'self'; " +
                                                "script-src 'self' https://accounts.google.com; " +
                                                "style-src 'self' 'unsafe-inline'; " +
                                                "img-src 'self' data: https://accounts.google.com; " +
                                                "frame-src https://accounts.google.com; " +
                                                "connect-src 'self' http://localhost:8081"
                                )
                        )
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                        .httpStrictTransportSecurity(hsts ->
                                hsts.includeSubDomains(true).maxAgeInSeconds(31536000)
                        )
                )

                // 🔥 IMPORTANT: prevent redirect loops
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                        )
                )

                // 🔐 ROUTES
                .authorizeHttpRequests(auth -> auth

                        // ✅ PUBLIC
                        .requestMatchers(
                                "/auth/**",
                                "/oauth2/**",
                                "/login/**",
                                "/error",
                                "/api/public/**",
                                "/api/user/me"   // 🔥 VERY IMPORTANT
                        ).permitAll()

                        // 🔒 ADMIN
                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        // 🔒 USER + ADMIN
                        .requestMatchers("/api/user/**")
                        .hasAnyRole("USER", "ADMIN")

                        .anyRequest().authenticated()
                )

                // 🔑 GOOGLE LOGIN
                .oauth2Login(oauth -> oauth
                        .successHandler(successHandler)
                )

                // 🔥 FILTER ORDER (VERY IMPORTANT)
                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                // 🚪 LOGOUT
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .deleteCookies("access_token", "refresh_token")
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        // 🔥 REQUIRED FOR COOKIES
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }
}