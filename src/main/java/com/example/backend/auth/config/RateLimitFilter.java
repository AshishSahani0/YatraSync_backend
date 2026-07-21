package com.example.backend.auth.config;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS = 100;
    private static final long WINDOW_SIZE = 60 * 1000;

    private final Map<String, Integer> requestCounts = new ConcurrentHashMap<>();
    private final Map<String, Long> lastResetTime = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        String method = request.getMethod();

        // 🔥 1. SKIP OPTIONS (CORS preflight)
        if ("OPTIONS".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 🔥 2. SKIP AUTH & PUBLIC ENDPOINTS
        if (isExcluded(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = getClientIP(request);
        long currentTime = System.currentTimeMillis();

        // 🧹 Memory Leak Fix: Evict stale entries if the maps grow large
        if (lastResetTime.size() > 500) {
            long threshold = currentTime - WINDOW_SIZE;
            lastResetTime.entrySet().removeIf(entry -> {
                if (entry.getValue() < threshold) {
                    requestCounts.remove(entry.getKey());
                    return true;
                }
                return false;
            });
        }

        lastResetTime.putIfAbsent(ip, currentTime);

        // 🔄 Reset window
        if (currentTime - lastResetTime.get(ip) > WINDOW_SIZE) {
            requestCounts.put(ip, 0);
            lastResetTime.put(ip, currentTime);
        }

        int currentCount = requestCounts.getOrDefault(ip, 0) + 1;
        requestCounts.put(ip, currentCount);

        // 📊 Headers
        response.setHeader("X-Rate-Limit-Limit", String.valueOf(MAX_REQUESTS));
        response.setHeader("X-Rate-Limit-Remaining",
                String.valueOf(Math.max(0, MAX_REQUESTS - currentCount)));

        // 🚫 BLOCK
        if (currentCount > MAX_REQUESTS) {
            response.setStatus(429);
            response.getWriter().write("Too many requests. Try again later.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    // ✅ EXCLUDED ROUTES (IMPORTANT)
    private boolean isExcluded(String uri) {
        return uri.startsWith("/auth")                 // login, refresh, logout
                || uri.startsWith("/api/user/me")      // auth check
                || uri.startsWith("/api/public")       // public APIs
                || uri.startsWith("/oauth2")           // OAuth flow
                || uri.startsWith("/error");           // Spring error path
    }

    // 🔍 Real IP (proxy-safe)
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isBlank()) {
            return xfHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}