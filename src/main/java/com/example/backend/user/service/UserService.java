package com.example.backend.user.service;

import com.example.backend.common.Role;
import com.example.backend.user.model.User;
import com.example.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    // 🔥 Create OR Update user (used in OAuth event)
    public User createOrUpdate(String email, String name) {

        return repository.findByEmail(email)
                .map(existingUser -> {
                    // 🔁 Update name if changed
                    if (!existingUser.getName().equals(name)) {
                        existingUser.setName(name);
                        return repository.save(existingUser);
                    }
                    return existingUser;
                })
                .orElseGet(() -> {
                    User user = new User();
                    user.setEmail(email);
                    user.setName(name);
                    user.setRole(Role.USER); // default role
                    return repository.save(user);
                });
    }

    // 🔍 Get user by email
    public User getByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}