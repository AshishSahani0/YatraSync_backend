package com.example.backend.user.model;

import com.example.backend.common.Role;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;


@Getter
@Setter
public class User {

    @Id
    private String id;

    private String name;

    private String email;

    private Role role = Role.USER;
}
