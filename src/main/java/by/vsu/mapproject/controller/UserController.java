package by.vsu.mapproject.controller;

import by.vsu.mapproject.entity.User;
import by.vsu.mapproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/api/user")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.ok().body(Map.of("authenticated", false));
        }

        String email = principal.getAttribute("email");
        User user = userRepository.findByEmail(email).orElse(null);

        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", true);
        response.put("email", email);
        response.put("name", principal.getAttribute("name"));
        response.put("picture", principal.getAttribute("picture"));

        if (user != null) {
            response.put("role", user.getRole());
            response.put("enabled", user.isEnabled());
            response.put("userId", user.getId());
        } else {
            response.put("role", "NEW");
            response.put("enabled", false);
        }

        return ResponseEntity.ok(response);
    }
}