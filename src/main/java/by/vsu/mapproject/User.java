package by.vsu.mapproject;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean enabled;

    private String googleId;
    private String calendarToken;
    private String calendarId; // ID календаря Google
    private String refreshToken;

    private LocalDateTime registrationDate;
    private LocalDateTime approvedDate;
}