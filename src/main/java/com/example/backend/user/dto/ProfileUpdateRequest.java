package com.example.backend.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUpdateRequest {
    private String userName;

    private String phoneNumber;

    private String bio;

    private String profileImage;

}
