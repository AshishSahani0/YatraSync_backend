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

    @Value("${app.env:dev}")
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

            // 📢 trigger user creation/update
            eventPublisher.publishEvent(new AuthSuccessEvent(email, name));

            // 🔥 fetch user AFTER event (important)
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 🔐 GENERATE TOKENS
            String accessToken = jwtService.generateToken(
                    user.getId(),
                    user.getEmail(),
                    user.getRole().name()
            );

            // 🔁 ALWAYS invalidate old refresh tokens
            String refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

            // 🍪 SET COOKIES
            addCookie(response, "access_token", accessToken, 15 * 60);
            addCookie(response, "refresh_token", refreshToken, 7 * 24 * 60 * 60);

            // 🔥 IMPORTANT: prevent caching issues
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Pragma", "no-cache");

            // 🔁 REDIRECT TO FRONTEND (WITH DYNAMIC ORIGIN FALLBACK)
            String origin = extractCookie(request, "frontend_origin");
            String targetUrl = (origin != null && !origin.isBlank()) ? origin : frontendUrl;
            deleteCookie(response, "frontend_origin");

            if (targetUrl != null && !targetUrl.contains("localhost") && !targetUrl.contains("127.0.0.1")) {
                String separator = targetUrl.contains("?") ? "&" : "?";
                targetUrl = targetUrl + separator + "token=" + accessToken + "&refresh_token=" + refreshToken;
            }
            response.sendRedirect(targetUrl);

        } catch (Exception e) {
            e.printStackTrace();

            // ❌ clear cookies if anything fails
            deleteCookie(response, "access_token");
            deleteCookie(response, "refresh_token");

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
            // ✅ PRODUCTION
            cookie = String.format(
                    "%s=%s; Max-Age=%d; Path=/; HttpOnly; SameSite=None; Secure",
                    name, value, maxAge
            );
        } else {
            // ✅ LOCALHOST (CRITICAL FIX)
            cookie = String.format(
                    "%s=%s; Max-Age=%d; Path=/; HttpOnly; SameSite=Lax",
                    name, value, maxAge
            );
        }

        response.addHeader("Set-Cookie", cookie);
    }

    private void deleteCookie(HttpServletResponse response, String name) {

        boolean isProd = "prod".equalsIgnoreCase(env);

        String cookie;

        if (isProd) {
            cookie = String.format(
                    "%s=; Max-Age=0; Path=/; HttpOnly; SameSite=None; Secure",
                    name
            );
        } else {
            cookie = String.format(
                    "%s=; Max-Age=0; Path=/; HttpOnly; SameSite=None",
                    name
            );
        }

        response.addHeader("Set-Cookie", cookie);
    }

    private String extractCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        return java.util.Arrays.stream(request.getCookies())
                .filter(c -> name.equals(c.getName()))
                .map(jakarta.servlet.http.Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}