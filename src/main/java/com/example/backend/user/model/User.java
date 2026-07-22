package com.example.backend.user.model;

import com.example.backend.common.Role;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.LocalDateTime;

@Getter
@Setter
public class User {

    @Id
    private String id;

    private String name;

    @Indexed(unique = true)
    private String userName;

    @Indexed(unique = true)
    private String email;

    private Role role = Role.USER;

    private String phoneNumber;
    private boolean isPhoneVerified = false;
    private String phoneUpdateAt; // leaving phoneUpdateAt as String or can convert later if needed, but phoneUpdateAt is less critical

    private String profileImage;

    private String bio;

    private boolean isVerified;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
