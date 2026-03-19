package by.vsu.mapproject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByGoogleId(String googleId);
    List<User> findByRoleAndEnabled(Role role, boolean enabled);

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.enabled = false")
    List<User> findPendingUsers(@Param("role") Role role);
}