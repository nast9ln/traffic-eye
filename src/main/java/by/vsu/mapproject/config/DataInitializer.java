package by.vsu.mapproject.config;

import by.vsu.mapproject.entity.User;
import by.vsu.mapproject.enums.Role;
import by.vsu.mapproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("manager").isEmpty()) {
            User manager = new User();
            manager.setUsername("manager");
            manager.setEmail("manager@example.com");
            manager.setPassword(passwordEncoder.encode("12345"));
            manager.setRole(Role.MANAGER);
            manager.setEnabled(true);

            userRepository.save(manager);
            System.out.println("✅ Менеджер создан: manager/12345");
        }

        if (userRepository.findByEmail("nastalen7@gmail.com").isEmpty()) {
            User employee = new User();
            employee.setUsername("Настя Лень");
            employee.setEmail("nastalen7@gmail.com");
            employee.setGoogleId("103224270869826154650"); // ID из лога
            employee.setRole(Role.MANAGER);
            employee.setEnabled(true);

            userRepository.save(employee);
            System.out.println("✅ Сотрудник создан: nastalen7@gmail.com");
        }
    }
}