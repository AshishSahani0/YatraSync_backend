package com.example.backend.auth.handler;

import com.example.backend.auth.event.AuthSuccessEvent;
import com.example.backend.auth.refresh.RefreshTokenService;
import com.example.backend.auth.service.JwtService;
import com.example.backend.user.model.User;
import com.example.backend.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final ApplicationEventPublisher eventPublisher;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${app.env:dev}") // 👉 dev or prod
    private String env;

    public OAuth2SuccessHandler(ApplicationEventPublisher eventPublisher,
                                JwtService jwtService,
                                UserRepository userRepository,
                                RefreshTokenService refreshTokenService) {
        this.eventPublisher = eventPublisher;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        try {
            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

            String email = Objects.toString(oauthUser.getAttribute("email"), null);
            String name = Objects.toString(oauthUser.getAttribute("name"), "User");

            if (email == null) {
                throw new RuntimeException("Email not found from OAuth provider");
            }

            // 📢 Event
            eventPublisher.publishEvent(new AuthSuccessEvent(email, name));

            // 🔥 Get user
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 🔑 Tokens
            String accessToken = jwtService.generateToken(email, user.getRole().name());
            String refreshToken = refreshTokenService.createRefreshToken(email);

            // 🍪 Cookies
            addCookie(response, "access_token", accessToken, 15 * 60);
            addCookie(response, "refresh_token", refreshToken, 7 * 24 * 60 * 60);

            // 🔁 Redirect
            response.sendRedirect(frontendUrl);

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
        }
    }


    private void addCookie(HttpServletResponse response,
                           String name,
                           String value,
                           int maxAge) {

        boolean isProd = "prod".equalsIgnoreCase(env);

        String cookie;

        if (isProd) {
            // 🌐 Production
            cookie = String.format(
                    "%s=%s; Max-Age=%d; Path=/; HttpOnly; SameSite=None; Secure",
                    name,
                    value,
                    maxAge
            );
        } else {
            // 🧪 LOCAL DEV (CRITICAL FIX)
            cookie = String.format(
                    "%s=%s; Max-Age=%d; Path=/; HttpOnly; SameSite=Lax",
                    name,
                    value,
                    maxAge
            );
        }

        response.addHeader("Set-Cookie", cookie);
    }
}