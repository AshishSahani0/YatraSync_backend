package com.example.backend.user.controller;

import com.example.backend.user.dto.ProfileUpdateRequest;
import com.example.backend.user.dto.ProfileUpdateResponse;
import com.example.backend.user.dto.UserResponse;
import com.example.backend.user.mapper.UserMapper;
import com.example.backend.user.model.User;
import com.example.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        String userId = authentication.getName();
        if (userId == null || "anonymousUser".equals(userId)) {
            return ResponseEntity.status(401).build();
        }
        User user = userService.getById(userId);


        if (user == null) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(UserMapper.mapToUserResponse(user));
    }

    @PutMapping("/profile-update")
    public ResponseEntity<ProfileUpdateResponse> updateProfile(
            @RequestBody ProfileUpdateRequest request,
            Authentication auth
    ) {

        String userId = auth.getName();

        User user = userService.updateProfile(userId, request);

        return ResponseEntity.ok(
                UserMapper.mapToProfileUpdateResponse(user)
        );
    }

}