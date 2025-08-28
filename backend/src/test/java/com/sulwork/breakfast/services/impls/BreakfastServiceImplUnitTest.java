package com.sulwork.breakfast.services.impls;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sulwork.breakfast.dtos.BreakfastRequestDTO;
import com.sulwork.breakfast.dtos.BreakfastResponseDTO;
import com.sulwork.breakfast.dtos.mappers.BreakfastMapper;
import com.sulwork.breakfast.persistence.model.Breakfast;
import com.sulwork.breakfast.persistence.repositories.BreakfastRepository;
import com.sulwork.breakfast.services.exceptions.BadRequestException;
import com.sulwork.breakfast.services.exceptions.NotFoundException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BreakfastServiceImplUnitTest {

    @Mock
    private BreakfastRepository repository;

    @InjectMocks
    private BreakfastServiceImpl service;

    private Breakfast breakfast;
    private BreakfastRequestDTO breakfastRequestDTO;
    private BreakfastResponseDTO breakfastResponseDTO;

    private Breakfast updatedBreakfast;
    private BreakfastRequestDTO updatedBreakfastRequestDTO;
    private BreakfastResponseDTO updatedBreakfastResponseDTO;

    @BeforeEach
    void setUp() {
        LocalDate date = LocalDate.of(2025, 8, 29);
        breakfast = new Breakfast(1L, date);
        breakfastRequestDTO = new BreakfastRequestDTO(date);
        breakfastResponseDTO = BreakfastMapper.toDto(breakfast);

        LocalDate updatedDate = LocalDate.of(2025, 9, 1);
        updatedBreakfast = new Breakfast(1L, updatedDate);
        updatedBreakfastRequestDTO = new BreakfastRequestDTO(updatedDate);
        updatedBreakfastResponseDTO = BreakfastMapper.toDto(updatedBreakfast);
    }

    @Test
    @DisplayName("should create a new breakfast successfully")
    void create_ShouldCreateBreakfast_WhenBreakfastDoesNotExist() {
        // Arrange
        when(repository.findByDateBreakfast(breakfastRequestDTO.getDate()))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(breakfast));
        doNothing().when(repository).createBreakfast(any());

        // Act
        BreakfastResponseDTO result = service.create(breakfastRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(breakfastResponseDTO.getDate(), result.getDate());
        verify(repository, times(1)).createBreakfast(breakfastRequestDTO.getDate());
        verify(repository, times(2)).findByDateBreakfast(breakfastRequestDTO.getDate());
    }

    @Test
    @DisplayName("should return a list of breakfasts successfully")
    void findAll_ShouldReturnListOfBreakfasts() {
        // Arrange
        List<Breakfast> breakfastList = Collections.singletonList(breakfast);
        when(repository.findAllBreakfasts()).thenReturn(breakfastList);

        // Act
        List<BreakfastResponseDTO> resultList = service.findAll();

        // Assert
        assertNotNull(resultList);
        assertFalse(resultList.isEmpty());
        assertEquals(1, resultList.size());
        assertEquals(breakfastResponseDTO.getDate(), resultList.get(0).getDate());
        verify(repository, times(1)).findAllBreakfasts();
    }

    @Test
    @DisplayName("should find a breakfast by ID successfully")
    void findById_ShouldReturnBreakfast_WhenBreakfastExists() {
        // Arrange
        when(repository.findByIdBreakfast(1L)).thenReturn(Optional.of(breakfast));

        // Act
        BreakfastResponseDTO result = service.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(breakfastResponseDTO.getDate(), result.getDate());
        verify(repository, times(1)).findByIdBreakfast(1L);
    }

    @Test
    @DisplayName("should throw NotFoundException when finding a breakfast by ID that does not exist")
    void findById_ShouldThrowNotFoundException_WhenBreakfastDoesNotExist() {
        // Arrange
        when(repository.findByIdBreakfast(2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.findById(2L));
        verify(repository, times(1)).findByIdBreakfast(2L);
    }

    @Test
    @DisplayName("should find a breakfast by date successfully")
    void findByDate_ShouldReturnBreakfast_WhenBreakfastExists() {
        // Arrange
        LocalDate date = LocalDate.of(2025, 8, 28);
        when(repository.findByDateBreakfast(date)).thenReturn(Optional.of(breakfast));

        // Act
        BreakfastResponseDTO result = service.findByDate(date);

        // Assert
        assertNotNull(result);
        assertEquals(breakfastResponseDTO.getDate(), result.getDate());
        verify(repository, times(1)).findByDateBreakfast(date);
    }

    @Test
    @DisplayName("should throw NotFoundException when finding a breakfast by date that does not exist")
    void findByDate_ShouldThrowNotFoundException_WhenBreakfastDoesNotExist() {
        // Arrange
        LocalDate date = LocalDate.of(2025, 8, 29);
        when(repository.findByDateBreakfast(date)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.findByDate(date));
        verify(repository, times(1)).findByDateBreakfast(date);
    }

    @Test
    @DisplayName("should update a breakfast successfully with a new date")
    void update_ShouldUpdateBreakfast_WhenBreakfastExistsAndDateIsNew() {
        // Arrange
        when(repository.findByIdBreakfast(1L))
                .thenReturn(Optional.of(breakfast))
                .thenReturn(Optional.of(updatedBreakfast));
        when(repository.findByDateBreakfast(updatedBreakfastRequestDTO.getDate())).thenReturn(Optional.empty());
        doNothing().when(repository).updateBreakfast(anyLong(), any(LocalDate.class));

        // Act
        BreakfastResponseDTO result = service.update(1L, updatedBreakfastRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(updatedBreakfastResponseDTO.getDate(), result.getDate());
        verify(repository, times(1)).findByIdBreakfast(1L);
        verify(repository, times(1)).findByDateBreakfast(updatedBreakfastRequestDTO.getDate());
        verify(repository, times(1)).updateBreakfast(1L, updatedBreakfastRequestDTO.getDate());
    }

    @Test
    @DisplayName("should update a breakfast successfully with the same date")
    void update_ShouldUpdateBreakfast_WhenBreakfastExistsAndDateIsSame() {
        // Arrange
        when(repository.findByIdBreakfast(1L))
                .thenReturn(Optional.of(breakfast))
                .thenReturn(Optional.of(breakfast));
        doNothing().when(repository).updateBreakfast(anyLong(), any(LocalDate.class));

        // Act
        BreakfastResponseDTO result = service.update(1L, breakfastRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(breakfastResponseDTO.getDate(), result.getDate());
        verify(repository, times(1)).findByIdBreakfast(1L);
        verify(repository, never()).findByDateBreakfast(any(LocalDate.class));
        verify(repository, times(1)).updateBreakfast(1L, breakfastRequestDTO.getDate());
    }

    @Test
    @DisplayName("should throw NotFoundException when trying to update a breakfast that does not exist")
    void update_ShouldThrowNotFoundException_WhenBreakfastDoesNotExist() {
        // Arrange
        when(repository.findByIdBreakfast(2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.update(2L, breakfastRequestDTO));
        verify(repository, times(1)).findByIdBreakfast(2L);
        verify(repository, never()).updateBreakfast(anyLong(), any(LocalDate.class));
    }

    @Test
    @DisplayName("should throw BadRequestException when trying to update a breakfast with an existing date")
    void update_ShouldThrowBadRequestException_WhenNewDateAlreadyExists() {
        // Arrange
        LocalDate newDate = LocalDate.of(2025, 9, 1);
        Breakfast existingBreakfastWithOtherDate = new Breakfast(2L, newDate);

        when(repository.findByIdBreakfast(1L)).thenReturn(Optional.of(breakfast));
        when(repository.findByDateBreakfast(newDate)).thenReturn(Optional.of(existingBreakfastWithOtherDate));

        BreakfastRequestDTO updateDto = new BreakfastRequestDTO(newDate);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> service.update(1L, updateDto));
        verify(repository, times(1)).findByIdBreakfast(1L);
        verify(repository, times(1)).findByDateBreakfast(newDate);
        verify(repository, never()).updateBreakfast(anyLong(), any(LocalDate.class));
    }

    @Test
    @DisplayName("should delete a breakfast successfully")
    void delete_ShouldDeleteBreakfast_WhenBreakfastExists() {
        // Arrange
        when(repository.findByIdBreakfast(1L)).thenReturn(Optional.of(breakfast));
        doNothing().when(repository).deleteBreakfast(1L);

        // Act
        service.delete(1L);

        // Assert
        verify(repository, times(1)).findByIdBreakfast(1L);
        verify(repository, times(1)).deleteBreakfast(1L);
    }

    @Test
    @DisplayName("should throw NotFoundException when trying to delete a breakfast that does not exist")
    void delete_ShouldThrowNotFoundException_WhenBreakfastDoesNotExist() {
        // Arrange
        when(repository.findByIdBreakfast(2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.delete(2L));
        verify(repository, times(1)).findByIdBreakfast(2L);
        verify(repository, never()).deleteBreakfast(anyLong());
    }
}