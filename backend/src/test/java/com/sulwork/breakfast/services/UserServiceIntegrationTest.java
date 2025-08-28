package com.sulwork.breakfast.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.sulwork.breakfast.dtos.UserRequestDTO;
import com.sulwork.breakfast.dtos.UserResponseDTO;
import com.sulwork.breakfast.persistence.enuns.Role;
import com.sulwork.breakfast.persistence.model.User;
import com.sulwork.breakfast.persistence.repositories.UserRepository;
import com.sulwork.breakfast.services.exceptions.BadRequestException;
import com.sulwork.breakfast.services.exceptions.NotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserService service;

    @Autowired
    private UserRepository repository;

    private User user;
    private UserRequestDTO dto;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        // Arrange
        user = new User(null, "João da Silva", "12345678900", "senha123", Role.USER);
        repository.createUser(user.getName(), user.getCpf(), user.getPassword(), user.getRole().toString());
        dto = new UserRequestDTO("Maria Oliveira", "98765432100", "senha456");
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("should create a new User in the database")
    void create_ShouldSaveUser_InDatabase() {
        // Act
        UserResponseDTO createdUser = service.create(dto);

        // Assert
        assertNotNull(createdUser);
        assertEquals(dto.getCpf(), createdUser.getCpf());
        assertEquals(dto.getName(), createdUser.getName());

        Optional<User> foundUser = repository.findByCpfUser(createdUser.getCpf());
        assertTrue(foundUser.isPresent());
        assertEquals(dto.getCpf(), foundUser.get().getCpf());
    }

    @Test
    @DisplayName("should throw BadRequestException when trying to create a user with an existing CPF")
    void create_ShouldThrowBadRequestException_WhenCpfAlreadyExists() {
        // Arrange
        UserRequestDTO existingCpfDto = new UserRequestDTO("Pedro", "12345678900", "senha789");

        // Act & Assert
        assertThrows(BadRequestException.class, () -> service.create(existingCpfDto));
    }

    @Test
    @DisplayName("should find all users from the database")
    void findAll_ShouldReturnAllUsers() {
        // Act
        List<UserResponseDTO> users = service.findAll();

        // Assert
        assertNotNull(users);
        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
    }

    @Test
    @DisplayName("should find a user by ID from the database")
    void findById_ShouldReturnUser_WhenIdExists() {
        // Arrange
        User savedUser = repository.findByCpfUser("12345678900").orElseThrow();

        // Act
        UserResponseDTO foundUser = service.findById(savedUser.getId());

        // Assert
        assertNotNull(foundUser);
        assertEquals(savedUser.getId(), foundUser.getId());
        assertEquals(savedUser.getCpf(), foundUser.getCpf());
    }

    @Test
    @DisplayName("should throw NotFoundException when finding a user by a non-existent ID")
    void findById_ShouldThrowNotFoundException_WhenIdDoesNotExist() {
        // Arrange
        Long nonExistentId = 99L;

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.findById(nonExistentId));
    }

    @Test
    @DisplayName("should find a user by CPF from the database")
    void findByCpf_ShouldReturnUser_WhenCpfExists() {
        // Arrange
        String existingCpf = "12345678900";

        // Act
        UserResponseDTO foundUser = service.findByCpf(existingCpf);

        // Assert
        assertNotNull(foundUser);
        assertEquals(existingCpf, foundUser.getCpf());
    }

    @Test
    @DisplayName("should throw NotFoundException when finding a user by a non-existent CPF")
    void findByCpf_ShouldThrowNotFoundException_WhenCpfDoesNotExist() {
        // Arrange
        String nonExistentCpf = "12332145673";

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.findByCpf(nonExistentCpf));
    }

    @Test
    @DisplayName("should update an existing user in the database")
    void update_ShouldUpdateUser_WhenIdExists() {
        // Arrange
        User savedUser = repository.findByCpfUser("12345678900").orElseThrow();
        String newName = "João da Silva Novo";
        String newCpf = "11122233344";
        UserRequestDTO updateDto = new UserRequestDTO(newName, newCpf, "senha123");

        // Act
        UserResponseDTO updatedUser = service.update(savedUser.getId(), updateDto);

        // Assert
        assertNotNull(updatedUser);
        assertEquals(newName, updatedUser.getName());
        assertEquals(newCpf, updatedUser.getCpf());

        Optional<User> foundUser = repository.findByIdUser(savedUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals(newName, foundUser.get().getName());
        assertEquals(newCpf, foundUser.get().getCpf());
    }

    @Test
    @DisplayName("should throw BadRequestException when trying to update a user with an existing CPF")
    void update_ShouldThrowBadRequestException_WhenNewCpfAlreadyExists() {
        // Arrange
        User savedUser = repository.findByCpfUser("12345678900").orElseThrow();
        repository.createUser("Outro Usuário", "11122233344", "senha123", "USER");

        UserRequestDTO updateDto = new UserRequestDTO("João Novo", "11122233344", "senha123");

        // Act & Assert
        assertThrows(BadRequestException.class, () -> service.update(savedUser.getId(), updateDto));
    }

    @Test
    @DisplayName("should delete an existing user from the database")
    void delete_ShouldRemoveUser_WhenIdExists() {
        // Arrange
        User savedUser = repository.findByCpfUser("12345678900").orElseThrow();
        Long userId = savedUser.getId();

        // Act
        service.delete(userId);

        // Assert
        Optional<User> deletedUser = repository.findByIdUser(userId);
        assertFalse(deletedUser.isPresent());
    }

    @Test
    @DisplayName("should throw NotFoundException when trying to delete a non-existent user")
    void delete_ShouldThrowNotFoundException_WhenIdDoesNotExist() {
        // Arrange
        Long nonExistentId = 99L;

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.delete(nonExistentId));
    }
}