package com.example.backend.auth.event;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AuthSuccessEvent {

    private String email;
    private String name;

}
