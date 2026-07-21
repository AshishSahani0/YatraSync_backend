package com.example.backend.user.mapper;

import com.example.backend.user.dto.ProfileUpdateResponse;
import com.example.backend.user.dto.UserResponse;
import com.example.backend.user.model.User;

public class UserMapper {

    public static ProfileUpdateResponse mapToProfileUpdateResponse(User user) {
        if (user == null) {
            return null;
        }

        ProfileUpdateResponse res = new ProfileUpdateResponse();

        res.setId(user.getId());
        res.setName(user.getName());
        res.setUserName(user.getUserName());
        res.setPhoneNumber(user.getPhoneNumber());
        res.setPhoneVerified(user.isPhoneVerified()); // 🔥 ADD THIS
        res.setProfileImage(user.getProfileImage());
        res.setBio(user.getBio());
        res.setVerified(user.isVerified());

        return res;
    }

    public static UserResponse mapToUserResponse(User user) {
        if (user == null) return null;

        UserResponse res = new UserResponse();

        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setRole(user.getRole() != null ? user.getRole().name() : null);

        res.setUserName(user.getUserName());
        res.setProfileImage(user.getProfileImage());
        res.setBio(user.getBio());

        return res;
    }
}