package com.sulwork.breakfast.services;

import com.sulwork.breakfast.dtos.UserRequestDTO;
import com.sulwork.breakfast.dtos.UserResponseDTO;
import com.sulwork.breakfast.persistence.enuns.Role;
import com.sulwork.breakfast.persistence.model.User;
import com.sulwork.breakfast.persistence.repositories.UserRepository;
import com.sulwork.breakfast.services.exceptions.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private UserRequestDTO userRequestDTO;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        // Arrange
        user = new User(null, "João", "12365423190", "password123", Role.USER);
        userRepository.createUser(user.getName(), user.getCpf(), user.getPassword(), user.getRole().name());

        userRequestDTO = new UserRequestDTO("Maria", "32176589283", "newpassword");
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("should create a new user in the database")
    void create_ShouldSaveUser_InDatabase() {
        // Act
        UserResponseDTO createdUser = userService.create(userRequestDTO);

        // Assert
        assertNotNull(createdUser);
        assertEquals(userRequestDTO.getCpf(), createdUser.getCpf());

        Optional<User> foundUser = userRepository.findByCpfUser(createdUser.getCpf());
        assertTrue(foundUser.isPresent());
        assertEquals(userRequestDTO.getName(), foundUser.get().getName());
    }

    @Test
    @DisplayName("should find all users from the database")
    void findAll_ShouldReturnAllUsers() {
        // Act
        List<UserResponseDTO> users = userService.findAll();

        // Assert
        assertNotNull(users);
        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
    }

    @Test
    @DisplayName("should find a user by ID from the database")
    void findById_ShouldReturnUser_WhenIdExists() {
        User savedUser = userRepository.findByCpfUser("12365423190").orElseThrow();

        // Act
        UserResponseDTO foundUser = userService.findById(savedUser.getId());

        // Assert
        assertNotNull(foundUser);
        assertEquals(savedUser.getId(), foundUser.getId());
        assertEquals(savedUser.getName(), foundUser.getName());
    }

    @Test
    @DisplayName("should throw NotFoundException when finding a user by a non-existent ID")
    void findById_ShouldThrowNotFoundException_WhenIdDoesNotExist() {
        // Arrange
        Long nonExistentId = 99L;

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userService.findById(nonExistentId));
    }
}