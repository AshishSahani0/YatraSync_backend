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

    private static final int MAX_REQUESTS = 100; // limit per window
    private static final long WINDOW_SIZE = 60 * 1000; // 1 minute

    private final Map<String, Integer> requestCounts = new ConcurrentHashMap<>();
    private final Map<String, Long> lastResetTime = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain)
            throws ServletException, IOException {

        String ip = getClientIP(request);

        long currentTime = System.currentTimeMillis();

        // 🔄 Reset counter if window expired
        lastResetTime.putIfAbsent(ip, currentTime);

        if (currentTime - lastResetTime.get(ip) > WINDOW_SIZE) {
            requestCounts.put(ip, 0);
            lastResetTime.put(ip, currentTime);
        }

        // ➕ Increment request count
        requestCounts.put(ip, requestCounts.getOrDefault(ip, 0) + 1);

        int currentCount = requestCounts.get(ip);

        // 📊 Add headers (important for frontend)
        response.setHeader("X-Rate-Limit-Limit", String.valueOf(MAX_REQUESTS));
        response.setHeader("X-Rate-Limit-Remaining",
                String.valueOf(Math.max(0, MAX_REQUESTS - currentCount)));

        // 🚫 Block if exceeded
        if (currentCount > MAX_REQUESTS) {
            response.setStatus(429);
            response.getWriter().write("Too many requests. Try again later.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    // 🔍 Handle proxy / real IP
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null) {
            return xfHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}