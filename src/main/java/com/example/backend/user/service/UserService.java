package com.example.backend.user.service;

import com.example.backend.common.Role;
import com.example.backend.media.service.MediaService;
import com.example.backend.user.dto.ProfileUpdateRequest;
import com.example.backend.user.model.User;
import com.example.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;

    private final MediaService mediaService;

    public UserService(UserRepository repository, MediaService mediaService) {
        this.repository = repository;
        this.mediaService = mediaService;
    }


    public User createOrUpdate(String email, String name) {

        return repository.findByEmail(email)
                .map(existingUser -> {

                    if (name != null && !name.equals(existingUser.getName())) {
                        existingUser.setName(name);
                        existingUser.setUpdatedAt(java.time.LocalDateTime.now());
                        return repository.save(existingUser);
                    }

                    return existingUser;
                })
                .orElseGet(() -> {
                    User user = new User();

                    user.setEmail(email);
                    user.setName(name != null ? name : "User");
                    user.setRole(Role.USER);

                    user.setCreatedAt(java.time.LocalDateTime.now());
                    user.setUpdatedAt(java.time.LocalDateTime.now());

                    return repository.save(user);
                });
    }


    public User getById(String userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


    public User updateProfile(String userId, ProfileUpdateRequest req) {

        User user = getById(userId);


        if (req.getUserName() != null &&
                !req.getUserName().equals(user.getUserName())) {

            String newUserName = normalizeUsername(req.getUserName());

            if (!isValidUsername(newUserName)) {
                throw new RuntimeException("Invalid username");
            }

            if (repository.existsByUserName(newUserName)) {
                throw new RuntimeException("Username already exists");
            }

            user.setUserName(newUserName);
        }

        // 🔹 PHONE
        if (req.getPhoneNumber() != null &&
                !req.getPhoneNumber().equals(user.getPhoneNumber())) {

            user.setPhoneNumber(req.getPhoneNumber());
            user.setPhoneVerified(false);
            user.setPhoneUpdateAt(now());
        }


        if (req.getBio() != null) {
            if (req.getBio().length() > 150) {
                throw new RuntimeException("Bio too long");
            }
            user.setBio(req.getBio());
        }


        if (req.getProfileImage() != null) {

            String oldImage = user.getProfileImage();

            user.setProfileImage(req.getProfileImage());

            user.setUpdatedAt(java.time.LocalDateTime.now());

            User savedUser = repository.save(user);

            // 🔥 delete old AFTER saving new
            if (oldImage != null && !oldImage.equals(req.getProfileImage())) {
                mediaService.deleteFileByUrl(oldImage);
            }

            return savedUser;
        }

        user.setUpdatedAt(java.time.LocalDateTime.now());

        return repository.save(user);
    }

    // =========================
    // 🔧 HELPERS
    // =========================

    private String normalizeUsername(String username) {
        return username.toLowerCase().trim();
    }

    private boolean isValidUsername(String username) {
        return username.matches("^[a-z0-9._]{3,20}$");
    }

    private String now() {
        return java.time.Instant.now().toString();
    }
}