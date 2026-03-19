package by.vsu.mapproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // РАЗРЕШАЕМ ВСЕ ЗАПРОСЫ
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                // ВКЛЮЧАЕМ OAuth2 (БЕЗ ПРОВЕРКИ АУТЕНТИФИКАЦИИ)
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login.html")
                        .defaultSuccessUrl("/dashboard", true)
                )
                // ВКЛЮЧАЕМ FORM LOGIN
                .formLogin(form -> form
                        .loginPage("/login.html")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )
                // ОТКЛЮЧАЕМ CSRF (ДЛЯ РАЗРАБОТКИ)
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}