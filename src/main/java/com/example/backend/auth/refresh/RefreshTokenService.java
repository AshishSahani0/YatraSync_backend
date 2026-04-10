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

    private final MongoTemplate mongoTemplate;

    public RefreshTokenService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    // 🔐 CREATE TOKEN (HASHED)
    public String createRefreshToken(String email) {

        String rawToken = UUID.randomUUID().toString();
        String hashedToken = DigestUtils.sha256Hex(rawToken);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setEmail(email);
        refreshToken.setToken(hashedToken);
        refreshToken.setExpiryDate(
                new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)
        );

        mongoTemplate.save(refreshToken);

        return rawToken; // 🔥 return RAW token to client
    }

    // 🔐 VALIDATE TOKEN
    public RefreshToken validateToken(String token) {

        if (token == null) {
            throw new RuntimeException("Refresh token missing");
        }

        String hashedToken = DigestUtils.sha256Hex(token);

        Query query = new Query(Criteria.where("token").is(hashedToken));
        RefreshToken rt = mongoTemplate.findOne(query, RefreshToken.class);

        if (rt == null) {
            throw new RuntimeException("Invalid refresh token");
        }

        if (rt.getExpiryDate().before(new Date())) {
            // 🔥 delete expired token
            mongoTemplate.remove(query, RefreshToken.class);
            throw new RuntimeException("Refresh token expired");
        }

        return rt;
    }

    // 🔁 ROTATE TOKEN (VERY IMPORTANT)
    public String rotateRefreshToken(String oldToken) {

        String hashedOld = DigestUtils.sha256Hex(oldToken);

        Query query = new Query(Criteria.where("token").is(hashedOld));
        RefreshToken existing = mongoTemplate.findOne(query, RefreshToken.class);

        if (existing == null) {
            throw new RuntimeException("Invalid refresh token");
        }

        // 🔥 delete old token
        mongoTemplate.remove(query, RefreshToken.class);

        // 🔥 create new token
        return createRefreshToken(existing.getEmail());
    }

    // 🧹 DELETE ALL TOKENS FOR USER (LOGOUT ALL DEVICES)
    public void deleteByEmail(String email) {
        Query query = new Query(Criteria.where("email").is(email));
        mongoTemplate.remove(query, RefreshToken.class);
    }
}