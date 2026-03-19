package by.vsu.mapproject.controller;

import by.vsu.mapproject.dto.RegisterRequest;
import by.vsu.mapproject.entity.User;
import by.vsu.mapproject.repository.UserRepository;
import by.vsu.mapproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class UnifiedAuthController {

    private final UserService userService;
    private final UserRepository userRepository;

    // REST API endpoint для регистрации
    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.registerUser(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getGoogleId(),
                    request.getCalendarId()
            );
            return ResponseEntity.ok().body("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Перенаправление на статические HTML страницы
    @GetMapping("/dashboard")
    public String dashboard() {
        return "forward:/index.html";
    }

    @GetMapping("/login")
    public String login() {
        return "forward:/login.html";
    }

    @GetMapping("/")
    public String home() {
        return "forward:/index.html";
    }

    @GetMapping("/api/debug/auth")
    @ResponseBody
    public ResponseEntity<?> debugAuth(@AuthenticationPrincipal OAuth2User principal) {
        Map<String, Object> response = new HashMap<>();

        if (principal == null) {
            response.put("authenticated", false);
            return ResponseEntity.ok(response);
        }

        String email = principal.getAttribute("email");
        User user = userRepository.findByEmail(email).orElse(null);

        response.put("authenticated", true);
        response.put("email", email);
        response.put("userInDb", user != null);

        if (user != null) {
            response.put("db_role", user.getRole());
            response.put("db_role_with_prefix", "ROLE_" + user.getRole());
            response.put("enabled", user.isEnabled());
        }

        // Получаем authorities из SecurityContext
        var authorities = principal.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.toList());

        response.put("oauth2_authorities", authorities);

        // Проверяем, есть ли ROLE_MANAGER в authorities
        boolean hasManagerRole = authorities.contains("ROLE_MANAGER");
        response.put("has_rolemanger_in_authorities", hasManagerRole);

        return ResponseEntity.ok(response);
    }
}