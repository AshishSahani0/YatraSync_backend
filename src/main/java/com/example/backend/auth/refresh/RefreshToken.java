package com.example.backend.auth.refresh;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "refresh_tokens")
@Getter
@Setter
public class RefreshToken {

    @Id
    private String id;

    private String email;
    private String token;
    private Date expiryDate;
}
