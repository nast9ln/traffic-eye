package by.vsu.mapproject;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String googleId;
    private String calendarToken;
    private String calendarId;
}