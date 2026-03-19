package by.vsu.mapproject.dto;

import by.vsu.mapproject.enums.Role;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private boolean enabled;
    private LocalDateTime registrationDate;
}