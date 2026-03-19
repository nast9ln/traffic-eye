package by.vsu.mapproject.controller;

import by.vsu.mapproject.entity.User;
import by.vsu.mapproject.enums.Role;
import by.vsu.mapproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oauth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String googleId = (String) attributes.get("sub");

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            user = new User();
            user.setUsername(name != null ? name : email);
            user.setEmail(email);
            user.setGoogleId(googleId);
            user.setRole(Role.NEW);
            user.setEnabled(false);
            user.setRegistrationDate(LocalDateTime.now());

            userRepository.save(user);
            System.out.println("✅ Новый пользователь создан: " + email + " с ролью NEW");
        } else {
            System.out.println("✅ Пользователь найден: " + email + " с ролью " + user.getRole());
        }

        return oauth2User;
    }
}