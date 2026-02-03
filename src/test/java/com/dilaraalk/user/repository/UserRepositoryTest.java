package com.dilaraalk.user.repository;

import com.dilaraalk.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.dilaraalk.config.TestMailConfig;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestMailConfig.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUserName_ShouldReturnUser_WhenUserExists() {
        // Arrange (Hazırlık)
        User user = new User();
        user.setUserName("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setRoles(java.util.Collections.singletonList("ROLE_USER")); // Enum değil, List<String>

        userRepository.save(user);

        // Act (Eylem)
        Optional<User> foundUser = userRepository.findByUserName("testuser");

        // Assert (Doğrulama)
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenUserExists() {
        // Arrange
        User user = new User();
        user.setUserName("emailuser");
        user.setEmail("email@example.com");
        user.setPassword("pass");

        userRepository.save(user);

        // Act
        Optional<User> foundUser = userRepository.findByEmail("email@example.com");

        // Assert
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUserName()).isEqualTo("emailuser");
    }

    @Test
    void findByUserName_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // Act
        Optional<User> foundUser = userRepository.findByUserName("nonexistent");

        // Assert
        assertThat(foundUser).isEmpty();
    }
}
