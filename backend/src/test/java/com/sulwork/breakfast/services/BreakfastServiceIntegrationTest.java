package com.sulwork.breakfast.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.sulwork.breakfast.dtos.BreakfastRequestDTO;
import com.sulwork.breakfast.dtos.BreakfastResponseDTO;
import com.sulwork.breakfast.persistence.model.Breakfast;
import com.sulwork.breakfast.persistence.repositories.BreakfastRepository;
import com.sulwork.breakfast.services.exceptions.NotFoundException;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class BreakfastServiceIntegrationTest {

    @Autowired
    private BreakfastService service;

    @Autowired
    private BreakfastRepository repository;

    private Breakfast breakfast;
    private BreakfastRequestDTO dto;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        // Arrange
        LocalDate date = LocalDate.of(2025, 8, 28);
        breakfast = new Breakfast(null, date);
        repository.createBreakfast(breakfast.getDate());
        dto = new BreakfastRequestDTO(LocalDate.of(2025, 8, 29));
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("should create a new Breakfast in the database")
    void create_ShouldSaveBreakfast_InDatabase() {
        // Act
        BreakfastResponseDTO createdBreakfast = service.create(dto);

        // Assert
        assertNotNull(createdBreakfast);
        assertEquals(dto.getDate(), createdBreakfast.getDate());

        Optional<Breakfast> foundBreakfast = repository.findByDateBreakfast(createdBreakfast.getDate());
        assertTrue(foundBreakfast.isPresent());
        assertEquals(dto.getDate(), foundBreakfast.get().getDate());
    }

    @Test
    @DisplayName("should throw BadRequestException when trying to create a breakfast with an existing date")
    void create_ShouldThrowBadRequestException_WhenDateAlreadyExists() {
        // Arrange
        BreakfastRequestDTO existingDateDto = new BreakfastRequestDTO(LocalDate.of(2025, 8, 28));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> service.create(existingDateDto));
    }

    @Test
    @DisplayName("should find all breakfasts from the database")
    void findAll_ShouldReturnAllBreakfasts() {
        // Act
        List<BreakfastResponseDTO> breakfasts = service.findAll();

        // Assert
        assertNotNull(breakfasts);
        assertFalse(breakfasts.isEmpty());
        assertEquals(1, breakfasts.size());
    }

    @Test
    @DisplayName("should find a breakfast by ID from the database")
    void findById_ShouldReturnBreakfast_WhenIdExists() {
        // Arrange
        Breakfast savedBreakfast = repository.findByDateBreakfast(LocalDate.of(2025, 8, 28)).orElseThrow();

        // Act
        BreakfastResponseDTO foundBreakfast = service.findById(savedBreakfast.getId());

        // Assert
        assertNotNull(foundBreakfast);
        assertEquals(savedBreakfast.getId(), foundBreakfast.getId());
        assertEquals(savedBreakfast.getDate(), foundBreakfast.getDate());
    }

    @Test
    @DisplayName("should throw NotFoundException when finding a breakfast by a non-existent ID")
    void findById_ShouldThrowNotFoundException_WhenIdDoesNotExist() {
        // Arrange
        Long nonExistentId = 99L;

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.findById(nonExistentId));
    }

    @Test
    @DisplayName("should find a breakfast by date from the database")
    void findByDate_ShouldReturnBreakfast_WhenDateExists() {
        // Arrange
        LocalDate existingDate = LocalDate.of(2025, 8, 28);

        // Act
        BreakfastResponseDTO foundBreakfast = service.findByDate(existingDate);

        // Assert
        assertNotNull(foundBreakfast);
        assertEquals(existingDate, foundBreakfast.getDate());
    }

    @Test
    @DisplayName("should throw NotFoundException when finding a breakfast by a non-existent date")
    void findByDate_ShouldThrowNotFoundException_WhenDateDoesNotExist() {
        // Arrange
        LocalDate nonExistentDate = LocalDate.of(2025, 1, 1);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.findByDate(nonExistentDate));
    }

    @Test
    @DisplayName("should update an existing breakfast in the database")
    void update_ShouldUpdateBreakfast_WhenIdExists() {
        // Arrange
        Breakfast savedBreakfast = repository.findByDateBreakfast(LocalDate.of(2025, 8, 28)).orElseThrow();
        LocalDate newDate = LocalDate.of(2025, 9, 1);
        BreakfastRequestDTO updateDto = new BreakfastRequestDTO(newDate);

        // Act
        BreakfastResponseDTO updatedBreakfast = service.update(savedBreakfast.getId(), updateDto);

        // Assert
        assertNotNull(updatedBreakfast);
        assertEquals(newDate, updatedBreakfast.getDate());

        Optional<Breakfast> foundBreakfast = repository.findByIdBreakfast(savedBreakfast.getId());
        assertTrue(foundBreakfast.isPresent());
        assertEquals(newDate, foundBreakfast.get().getDate());
    }

    @Test
    @DisplayName("should delete an existing breakfast from the database")
    void delete_ShouldRemoveBreakfast_WhenIdExists() {
        // Arrange
        Breakfast savedBreakfast = repository.findByDateBreakfast(LocalDate.of(2025, 8, 28)).orElseThrow();
        Long breakfastId = savedBreakfast.getId();

        // Act
        service.delete(breakfastId);

        // Assert
        Optional<Breakfast> deletedBreakfast = repository.findByIdBreakfast(breakfastId);
        assertFalse(deletedBreakfast.isPresent());
    }

    @Test
    @DisplayName("should throw NotFoundException when trying to delete a non-existent breakfast")
    void delete_ShouldThrowNotFoundException_WhenIdDoesNotExist() {
        // Arrange
        Long nonExistentId = 99L;

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.delete(nonExistentId));
    }
}