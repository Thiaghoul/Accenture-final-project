package com.group5.taskFlow.repository;

import com.group5.taskFlow.model.UserModels;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should save a user successfully")
    void saveUser_Success() {
        UserModels newUser = new UserModels();
        newUser.setEmail("test@example.com");
        newUser.setPasswordHash("password123");
        newUser.setFirstName("Test");
        newUser.setLastName("User");

        UserModels savedUser = userRepository.save(newUser);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should find a user by email")
    void findByEmail_WhenUserExists_ReturnsUser() {
        // given
        UserModels user = new UserModels();
        user.setEmail("findme@example.com");
        user.setPasswordHash("password");
        user.setFirstName("First");
        user.setLastName("Last");
        entityManager.persistAndFlush(user);

        // when
        Optional<UserModels> found = userRepository.findByEmail("findme@example.com");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("findme@example.com");
    }

    @Test
    @DisplayName("Should return empty optional when user with email does not exist")
    void findByEmail_WhenUserDoesNotExist_ReturnsEmpty() {
        // when
        Optional<UserModels> found = userRepository.findByEmail("donotfindme@example.com");

        // then
        assertThat(found).isNotPresent();
    }

    @Test
    @DisplayName("Should throw exception when saving a user with a non-unique email")
    void saveUser_WithDuplicateEmail_ThrowsException() {
        // given
        UserModels user1 = new UserModels();
        user1.setEmail("duplicate@example.com");
        user1.setPasswordHash("password");
        user1.setFirstName("First");
        user1.setLastName("Last");
        // Use saveAndFlush to ensure the first user is persisted and the unique constraint is active
        userRepository.saveAndFlush(user1);

        UserModels user2 = new UserModels();
        user2.setEmail("duplicate@example.com");
        user2.setPasswordHash("password");
        user2.setFirstName("Second");
        user2.setLastName("User");

        // when & then
        // Now, attempting to save the second user should trigger the exception
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user2);
        });
    }
}
