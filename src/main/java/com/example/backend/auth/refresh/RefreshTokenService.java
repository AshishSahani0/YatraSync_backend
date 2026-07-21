package com.example.backend.auth.refresh;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private static final long REFRESH_EXPIRY = 7L * 24 * 60 * 60 * 1000;

    private final MongoTemplate mongoTemplate;

    public RefreshTokenService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    // ✅ CREATE TOKEN (1 USER = 1 TOKEN)
    public String createRefreshToken(String email) {

        if (email == null || email.isBlank()) {
            throw new RuntimeException("Email required for refresh token");
        }

        // 🔥 delete old tokens (important)
        deleteByEmail(email);

        String rawToken = UUID.randomUUID().toString();

        if (rawToken == null || rawToken.isBlank()) {
            throw new RuntimeException("Failed to generate refresh token");
        }

        String hashedToken = hash(rawToken);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setEmail(email);
        refreshToken.setToken(hashedToken);
        refreshToken.setExpiryDate(
                new Date(System.currentTimeMillis() + REFRESH_EXPIRY)
        );

        mongoTemplate.save(refreshToken);

        return rawToken;
    }

    // ✅ VALIDATE TOKEN
    public RefreshToken validateToken(String rawToken) {

        if (rawToken == null || rawToken.isBlank()) {
            throw new RuntimeException("Refresh token missing");
        }

        String hashed = hash(rawToken);

        RefreshToken token = mongoTemplate.findOne(
                new Query(Criteria.where("token").is(hashed)),
                RefreshToken.class
        );

        if (token == null) {
            throw new RuntimeException("Invalid refresh token");
        }

        // ⏰ expiry check
        if (token.getExpiryDate().before(new Date())) {
            mongoTemplate.remove(
                    new Query(Criteria.where("token").is(hashed)),
                    RefreshToken.class
            );
            throw new RuntimeException("Refresh token expired");
        }

        return token;
    }

    // 🔁 ROTATE TOKEN (SECURITY CRITICAL)
    public String rotateRefreshToken(String oldRawToken) {

        RefreshToken existing = validateToken(oldRawToken);

        // 🔥 delete old
        mongoTemplate.remove(
                new Query(Criteria.where("token").is(hash(oldRawToken))),
                RefreshToken.class
        );

        return createRefreshToken(existing.getEmail());
    }

    // 🧹 DELETE USER TOKENS
    public void deleteByEmail(String email) {
        mongoTemplate.remove(
                new Query(Criteria.where("email").is(email)),
                RefreshToken.class
        );
    }

    // 🔐 HASH
    private String hash(String token) {
        return DigestUtils.sha256Hex(token);
    }
}