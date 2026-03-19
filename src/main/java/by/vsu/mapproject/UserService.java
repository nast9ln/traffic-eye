package by.vsu.mapproject;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(String username, String email, String password,
                             String googleId, String calendarId) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setGoogleId(googleId);
        user.setCalendarId(calendarId);
        user.setRole(Role.NEW);
        user.setEnabled(false);
        user.setRegistrationDate(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Transactional
    public User approveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole(Role.EMPLOYEE);
        user.setEnabled(true);
        user.setApprovedDate(LocalDateTime.now());

        return userRepository.save(user);
    }

    public List<UserDTO> getPendingUsers() {
        return userRepository.findByRoleAndEnabled(Role.NEW, false)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setEnabled(user.isEnabled());
        dto.setRegistrationDate(user.getRegistrationDate());
        return dto;
    }
}