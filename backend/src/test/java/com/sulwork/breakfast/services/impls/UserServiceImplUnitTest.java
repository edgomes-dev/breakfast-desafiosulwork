package com.sulwork.breakfast.services.impls;

import com.sulwork.breakfast.dtos.UserRequestDTO;
import com.sulwork.breakfast.dtos.UserResponseDTO;
import com.sulwork.breakfast.dtos.mappers.UserMapper;
import com.sulwork.breakfast.persistence.enuns.Role;
import com.sulwork.breakfast.persistence.model.User;
import com.sulwork.breakfast.persistence.repositories.UserRepository;
import com.sulwork.breakfast.services.exceptions.BadRequestException;
import com.sulwork.breakfast.services.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplUnitTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserRequestDTO userRequestDTO;
    private UserResponseDTO userResponseDTO;

    private User updatedUser;
    private UserRequestDTO updatedUserRequestDTO;
    private UserResponseDTO updatedUserResponseDTO;

    @BeforeEach
    void setUp() {
        user = new User(1L, "João da Silva", "12345678900", "senha123", Role.USER);
        userRequestDTO = new UserRequestDTO("João da Silva", "12345678900", "senha123");
        userResponseDTO = UserMapper.toDto(user);

        updatedUser = new User(1L, "João da Silva Atualizado", "09876543211", "senha123", Role.USER);
        updatedUserRequestDTO = new UserRequestDTO("João da Silva Atualizado", "09876543211", "senha123");
        updatedUserResponseDTO = UserMapper.toDto(updatedUser);
    }

    @Test
    @DisplayName("should create a new user successfully")
    void create_ShouldCreateUser_WhenCpfIsNotRegistered() {
        // Arrange
        when(repository.findByCpfUser(userRequestDTO.getCpf()))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(user));

        // Act
        UserResponseDTO result = userService.create(userRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(userResponseDTO.getName(), result.getName());
        assertEquals(userResponseDTO.getCpf(), result.getCpf());
        verify(repository, times(2)).findByCpfUser(userRequestDTO.getCpf());
        verify(repository, times(1)).createUser(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("should throw BadRequestException when creating a user with an existing CPF")
    void create_ShouldThrowBadRequestException_WhenCpfIsRegistered() {
        // Arrange
        when(repository.findByCpfUser(userRequestDTO.getCpf())).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> userService.create(userRequestDTO));
        verify(repository, times(1)).findByCpfUser(userRequestDTO.getCpf());
        verify(repository, never()).createUser(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("should throw BadRequestException when creating a user with a sequential CPF")
    void create_ShouldThrowBadRequestException_WhenCpfIsSequential() {
        // Arrange
        UserRequestDTO sequentialCpfDto = new UserRequestDTO("Nome", "11111111111", "senha123");

        // Act & Assert
        assertThrows(BadRequestException.class, () -> userService.create(sequentialCpfDto));
        verify(repository, never()).findByCpfUser(anyString());
        verify(repository, never()).createUser(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("should throw BadRequestException when creating a user with a short CPF")
    void create_ShouldThrowBadRequestException_WhenCpfIsTooShort() {
        // Arrange
        UserRequestDTO shortCpfDto = new UserRequestDTO("Nome", "12345", "senha123");

        // Act & Assert
        assertThrows(BadRequestException.class, () -> userService.create(shortCpfDto));
        verify(repository, never()).findByCpfUser(anyString());
        verify(repository, never()).createUser(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("should throw BadRequestException when creating a user with non-numeric CPF")
    void create_ShouldThrowBadRequestException_WhenCpfIsNotNumeric() {
        // Arrange
        UserRequestDTO nonNumericCpfDto = new UserRequestDTO("Nome", "abcde123456", "senha123");

        // Act & Assert
        assertThrows(BadRequestException.class, () -> userService.create(nonNumericCpfDto));
        verify(repository, never()).findByCpfUser(anyString());
        verify(repository, never()).createUser(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("should return a list of users successfully")
    void findAll_ShouldReturnListOfUsers() {
        // Arrange
        List<User> userList = Collections.singletonList(user);
        when(repository.findAllUsers()).thenReturn(userList);

        // Act
        List<UserResponseDTO> resultList = userService.findAll();

        // Assert
        assertNotNull(resultList);
        assertFalse(resultList.isEmpty());
        assertEquals(1, resultList.size());
        assertEquals(userResponseDTO.getName(), resultList.get(0).getName());
        verify(repository, times(1)).findAllUsers();
    }

    @Test
    @DisplayName("should find a user by ID successfully")
    void findById_ShouldReturnUser_WhenUserExists() {
        // Arrange
        when(repository.findByIdUser(1L)).thenReturn(Optional.of(user));

        // Act
        UserResponseDTO result = userService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(userResponseDTO.getName(), result.getName());
        verify(repository, times(1)).findByIdUser(1L);
    }

    @Test
    @DisplayName("should throw NotFoundException when finding a user by ID that does not exist")
    void findById_ShouldThrowNotFoundException_WhenUserDoesNotExist() {
        // Arrange
        when(repository.findByIdUser(2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userService.findById(2L));
        verify(repository, times(1)).findByIdUser(2L);
    }

    @Test
    @DisplayName("should find a user by CPF successfully")
    void findByCpf_ShouldReturnUser_WhenUserExists() {
        // Arrange
        when(repository.findByCpfUser("12345678900")).thenReturn(Optional.of(user));

        // Act
        UserResponseDTO result = userService.findByCpf("12345678900");

        // Assert
        assertNotNull(result);
        assertEquals(userResponseDTO.getCpf(), result.getCpf());
        verify(repository, times(1)).findByCpfUser("12345678900");
    }

    @Test
    @DisplayName("should throw NotFoundException when finding a user by CPF that does not exist")
    void findByCpf_ShouldThrowNotFoundException_WhenUserDoesNotExist() {
        // Arrange
        when(repository.findByCpfUser("98981242312")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userService.findByCpf("98981242312"));
        verify(repository, times(1)).findByCpfUser("98981242312");
    }

    @Test
    @DisplayName("should throw BadRequestException when searching for a sequential CPF")
    void findByCpf_ShouldThrowBadRequestException_WhenCpfIsSequential() {
        // Arrange
        String sequentialCpf = "11111111111";

        // Act & Assert
        assertThrows(BadRequestException.class, () -> userService.findByCpf(sequentialCpf));
        verify(repository, never()).findByCpfUser(anyString());
    }

    @Test
    @DisplayName("should throw BadRequestException when searching for a short CPF")
    void findByCpf_ShouldThrowBadRequestException_WhenCpfIsTooShort() {
        // Arrange
        String shortCpf = "12345";

        // Act & Assert
        assertThrows(BadRequestException.class, () -> userService.findByCpf(shortCpf));
        verify(repository, never()).findByCpfUser(anyString());
    }

    @Test
    @DisplayName("should throw BadRequestException when searching for a non-numeric CPF")
    void findByCpf_ShouldThrowBadRequestException_WhenCpfIsNotNumeric() {
        // Arrange
        String nonNumericCpf = "abcde123456";

        // Act & Assert
        assertThrows(BadRequestException.class, () -> userService.findByCpf(nonNumericCpf));
        verify(repository, never()).findByCpfUser(anyString());
    }

    @Test
    @DisplayName("should update a user successfully with same CPF")
    void update_ShouldUpdateUser_WhenUserExistsAndCpfIsSame() {
        // Arrange
        UserRequestDTO updateDto = new UserRequestDTO("João da Silva Novo Nome", "12345678900", "senha123");
        User updatedSameCpfUser = new User(1L, "João da Silva Novo Nome", "12345678900", "senha123", Role.USER);

        when(repository.findByIdUser(1L)).thenReturn(Optional.of(user));
        doNothing().when(repository).updateUser(anyLong(), anyString(), anyString());
        when(repository.findByIdUser(1L)).thenReturn(Optional.of(updatedSameCpfUser));

        // Act
        UserResponseDTO result = userService.update(1L, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals(updateDto.getName(), result.getName());
        assertEquals(updateDto.getCpf(), result.getCpf());
        verify(repository, times(1)).findByIdUser(1L);
        verify(repository, times(1)).updateUser(1L, updateDto.getName(), updateDto.getCpf());
        verify(repository, never()).findByCpfUser(anyString()); // Não deve chamar findByCpfUser
    }

    @Test
    @DisplayName("should update a user successfully with a new CPF")
    void update_ShouldUpdateUser_WhenUserExistsAndCpfIsNew() {
        // Arrange
        when(repository.findByIdUser(1L))
                .thenReturn(Optional.of(user))
                .thenReturn(Optional.of(updatedUser));

        when(repository.findByCpfUser(updatedUserRequestDTO.getCpf())).thenReturn(Optional.empty());
        doNothing().when(repository).updateUser(anyLong(), anyString(), anyString());

        // Act
        UserResponseDTO result = userService.update(1L, updatedUserRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(updatedUserResponseDTO.getName(), result.getName());
        assertEquals(updatedUserResponseDTO.getCpf(), result.getCpf());
        verify(repository, times(1)).findByIdUser(1L);
        verify(repository, times(1)).findByCpfUser(updatedUserRequestDTO.getCpf());
        verify(repository, times(1)).updateUser(1L, updatedUserRequestDTO.getName(), updatedUserRequestDTO.getCpf());
    }

    @Test
    @DisplayName("should throw NotFoundException when trying to update a user that does not exist")
    void update_ShouldThrowNotFoundException_WhenUserDoesNotExist() {
        // Arrange
        when(repository.findByIdUser(2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userService.update(2L, userRequestDTO));
        verify(repository, times(1)).findByIdUser(2L);
        verify(repository, never()).updateUser(anyLong(), anyString(), anyString());
    }

    @Test
    @DisplayName("should throw BadRequestException when trying to update a user with an existing CPF")
    void update_ShouldThrowBadRequestException_WhenNewCpfAlreadyExists() {
        // Arrange
        User existingUserWithOtherCpf = new User(2L, "Maria", "98765432100", "senha456", Role.USER);

        when(repository.findByIdUser(1L)).thenReturn(Optional.of(user));
        when(repository.findByCpfUser("98765432100")).thenReturn(Optional.of(existingUserWithOtherCpf));

        UserRequestDTO updateDto = new UserRequestDTO("João da Silva Novo Nome", "98765432100", "senha123");

        // Act & Assert
        assertThrows(BadRequestException.class, () -> userService.update(1L, updateDto));
        verify(repository, times(1)).findByIdUser(1L);
        verify(repository, times(1)).findByCpfUser("98765432100");
        verify(repository, never()).updateUser(anyLong(), anyString(), anyString());
    }

    @Test
    @DisplayName("should throw BadRequestException when updating a user with a sequential CPF")
    void update_ShouldThrowBadRequestException_WhenCpfIsSequential() {
        // Arrange
        UserRequestDTO sequentialCpfDto = new UserRequestDTO("Nome", "11111111111", "senha123");
        Long userId = 1L;

        // Act & Assert
        assertThrows(BadRequestException.class, () -> userService.update(userId, sequentialCpfDto));
        verify(repository, never()).findByIdUser(anyLong());
        verify(repository, never()).updateUser(anyLong(), anyString(), anyString());
    }

    @Test
    @DisplayName("should throw BadRequestException when updating a user with a short CPF")
    void update_ShouldThrowBadRequestException_WhenCpfIsTooShort() {
        // Arrange
        UserRequestDTO shortCpfDto = new UserRequestDTO("Nome", "12345", "senha123");
        Long userId = 1L;

        // Act & Assert
        assertThrows(BadRequestException.class, () -> userService.update(userId, shortCpfDto));
        verify(repository, never()).findByIdUser(anyLong());
        verify(repository, never()).updateUser(anyLong(), anyString(), anyString());
    }

    @Test
    @DisplayName("should throw BadRequestException when updating a user with non-numeric CPF")
    void update_ShouldThrowBadRequestException_WhenCpfIsNotNumeric() {
        // Arrange
        UserRequestDTO nonNumericCpfDto = new UserRequestDTO("Nome", "abcde123456", "senha123");
        Long userId = 1L;

        // Act & Assert
        assertThrows(BadRequestException.class, () -> userService.update(userId, nonNumericCpfDto));
        verify(repository, never()).findByIdUser(anyLong());
        verify(repository, never()).updateUser(anyLong(), anyString(), anyString());
    }

    @Test
    @DisplayName("should delete a user successfully")
    void delete_ShouldDeleteUser_WhenUserExists() {
        // Arrange
        when(repository.findByIdUser(1L)).thenReturn(Optional.of(user));
        doNothing().when(repository).deleteUser(1L);

        // Act
        userService.delete(1L);

        // Assert
        verify(repository, times(1)).findByIdUser(1L);
        verify(repository, times(1)).deleteUser(1L);
    }

    @Test
    @DisplayName("should throw NotFoundException when trying to delete a user that does not exist")
    void delete_ShouldThrowNotFoundException_WhenUserDoesNotExist() {
        // Arrange
        when(repository.findByIdUser(2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userService.delete(2L));
        verify(repository, times(1)).findByIdUser(2L);
        verify(repository, never()).deleteUser(anyLong());
    }
}