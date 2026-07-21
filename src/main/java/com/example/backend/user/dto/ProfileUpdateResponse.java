package com.example.backend.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUpdateResponse {

    private String id;
    private String name;

    private String userName;

    private String phoneNumber;
    private boolean isPhoneVerified;

    private String profileImage;
    private String bio;

    private boolean isVerified;
}
