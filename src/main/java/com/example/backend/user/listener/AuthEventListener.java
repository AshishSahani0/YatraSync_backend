package com.example.backend.user.listener;

import com.example.backend.auth.event.AuthSuccessEvent;
import com.example.backend.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AuthEventListener {

    private final UserService userService;

    public AuthEventListener(UserService userService) {
        this.userService = userService;
    }

    @EventListener
    public void handle(AuthSuccessEvent event) {
        userService.createOrUpdate(
                event.getEmail(),
                event.getName()
        );
    }
}
