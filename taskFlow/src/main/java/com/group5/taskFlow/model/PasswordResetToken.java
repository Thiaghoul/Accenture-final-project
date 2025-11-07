package com.group5.taskFlow.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class PasswordResetToken {

    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String token;

    @OneToOne(targetEntity = UserModels.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private UserModels user;

    private Date expiryDate;

    public PasswordResetToken(String token, UserModels user) {
        this.token = token;
        this.user = user;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        final long ONE_MINUTE_IN_MILLIS = 60000;
        long expiryTimeInMilliseconds = expiryTimeInMinutes * ONE_MINUTE_IN_MILLIS;
        return new Date(System.currentTimeMillis() + expiryTimeInMilliseconds);
    }
}
