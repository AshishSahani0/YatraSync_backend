package com.example.backend.auth.controller;

import com.example.backend.auth.refresh.RefreshToken;
import com.example.backend.auth.refresh.RefreshTokenService;
import com.example.backend.auth.service.JwtService;
import com.example.backend.user.model.User;
import com.example.backend.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Value("${app.env:dev}")
    private String env;

    public AuthController(RefreshTokenService refreshTokenService,
                          JwtService jwtService,
                          UserRepository userRepository) {
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request,
                                     HttpServletResponse response) {

        String refreshToken = Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(c -> "refresh_token".equals(c.getName()))
                        .map(Cookie::getValue)
                        .findFirst())
                .orElse(null);

        if (refreshToken == null) {
            return ResponseEntity.status(401).body("Refresh token missing");
        }

        RefreshToken rt;
        try {
            rt = refreshTokenService.validateToken(refreshToken);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid refresh token");
        }

        User user = userRepository.findByEmail(rt.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newRefreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        String newAccessToken = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        addCookie(response, "access_token", newAccessToken, 15 * 60);
        addCookie(response, "refresh_token", newRefreshToken, 7 * 24 * 60 * 60);

        return ResponseEntity.ok("Token refreshed");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request,
                                    HttpServletResponse response) {

        String refreshToken = Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(c -> "refresh_token".equals(c.getName()))
                        .map(Cookie::getValue)
                        .findFirst())
                .orElse(null);

        if (refreshToken != null) {
            try {
                RefreshToken rt = refreshTokenService.validateToken(refreshToken);
                refreshTokenService.deleteByEmail(rt.getEmail());
            } catch (Exception ignored) {}
        }

        deleteCookie(response, "access_token");
        deleteCookie(response, "refresh_token");

        return ResponseEntity.ok("Logged out");
    }

    private void addCookie(HttpServletResponse response,
                           String name,
                           String value,
                           int maxAge) {

        boolean isProd = "prod".equalsIgnoreCase(env);

        String cookie;

        if (isProd) {
            // 🌐 Production (HTTPS required)
            cookie = String.format(
                    "%s=%s; Max-Age=%d; Path=/; HttpOnly; SameSite=None; Secure",
                    name,
                    value,
                    maxAge
            );
        } else {
            // 🧪 Local development (IMPORTANT FIX)
            cookie = String.format(
                    "%s=%s; Max-Age=%d; Path=/; HttpOnly; SameSite=Lax",
                    name,
                    value,
                    maxAge
            );
        }

        response.addHeader("Set-Cookie", cookie);
    }

    private void deleteCookie(HttpServletResponse response, String name) {

        boolean isProd = "prod".equalsIgnoreCase(env);

        String cookie = isProd
                ? String.format("%s=; Max-Age=0; Path=/; HttpOnly; SameSite=None; Secure", name)
                : String.format("%s=; Max-Age=0; Path=/; HttpOnly; SameSite=Lax", name);

        response.addHeader("Set-Cookie", cookie);
    }
}