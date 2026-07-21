package com.example.backend.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {
    private String id;
    private String email;
    private String name;
    private String role;

    private String userName;
    private String profileImage;
    private String bio;
}
