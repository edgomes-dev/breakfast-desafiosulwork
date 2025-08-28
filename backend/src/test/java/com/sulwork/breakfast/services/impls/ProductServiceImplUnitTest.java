package com.sulwork.breakfast.services.impls;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sulwork.breakfast.dtos.ProductRequestDTO;
import com.sulwork.breakfast.dtos.ProductResponseDTO;
import com.sulwork.breakfast.dtos.mappers.ProductMapper;
import com.sulwork.breakfast.persistence.model.Product;
import com.sulwork.breakfast.persistence.repositories.ProductRepository;
import com.sulwork.breakfast.services.exceptions.BadRequestException;
import com.sulwork.breakfast.services.exceptions.NotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplUnitTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductServiceImpl service;

    private Product product;
    private ProductRequestDTO productRequestDTO;
    private ProductResponseDTO productResponseDTO;

    private Product updatedProduct;
    private ProductRequestDTO updatedProductRequestDTO;
    private ProductResponseDTO updatedProductResponseDTO;

    @BeforeEach
    void setUp() {
        product = new Product(1L, "Queijo");
        productRequestDTO = new ProductRequestDTO("queijo");
        productResponseDTO = ProductMapper.toDto(product);

        updatedProduct = new Product(1L, "Queijo Mussarela");
        updatedProductRequestDTO = new ProductRequestDTO("queijo Mussarela");
        updatedProductResponseDTO = ProductMapper.toDto(updatedProduct);
    }

    @Test
    @DisplayName("should create a new product successfully")
    void create_ShouldCreateProduct_WhenProductDoesNotExist() {
        // Arrange
        when(repository.findByNameProduct(productRequestDTO.getName()))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(product));
        doNothing().when(repository).createProduct(anyString());

        // Act
        ProductResponseDTO result = service.create(productRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(productResponseDTO.getName(), result.getName());
        verify(repository, times(1)).createProduct(productRequestDTO.getName());
        verify(repository, times(2)).findByNameProduct(productRequestDTO.getName());
    }

    @Test
    @DisplayName("should throw BadRequestException when creating a product with an existing name")
    void create_ShouldThrowBadRequestException_WhenProductAlreadyExists() {
        // Arrange
        when(repository.findByNameProduct(productRequestDTO.getName())).thenReturn(Optional.of(product));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> service.create(productRequestDTO));
        verify(repository, times(1)).findByNameProduct(productRequestDTO.getName());
        verify(repository, never()).createProduct(anyString());
    }

    @Test
    @DisplayName("should return a list of products successfully")
    void findAll_ShouldReturnListOfProducts() {
        // Arrange
        List<Product> productList = Collections.singletonList(product);
        when(repository.findAllProducts()).thenReturn(productList);

        // Act
        List<ProductResponseDTO> resultList = service.findAll();

        // Assert
        assertNotNull(resultList);
        assertFalse(resultList.isEmpty());
        assertEquals(1, resultList.size());
        assertEquals(productResponseDTO.getName(), resultList.get(0).getName());
        verify(repository, times(1)).findAllProducts();
    }

    @Test
    @DisplayName("should find a product by ID successfully")
    void findById_ShouldReturnProduct_WhenProductExists() {
        // Arrange
        when(repository.findByIdProduct(1L)).thenReturn(Optional.of(product));

        // Act
        ProductResponseDTO result = service.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(productResponseDTO.getName(), result.getName());
        verify(repository, times(1)).findByIdProduct(1L);
    }

    @Test
    @DisplayName("should throw NotFoundException when finding a product by ID that does not exist")
    void findById_ShouldThrowNotFoundException_WhenProductDoesNotExist() {
        // Arrange
        when(repository.findByIdProduct(2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.findById(2L));
        verify(repository, times(1)).findByIdProduct(2L);
    }

    @Test
    @DisplayName("should find a product by name successfully")
    void findByName_ShouldReturnProduct_WhenProductExists() {
        // Arrange
        when(repository.findByNameProduct("Queijo")).thenReturn(Optional.of(product));

        // Act
        ProductResponseDTO result = service.findByName("Queijo");

        // Assert
        assertNotNull(result);
        assertEquals(productResponseDTO.getName(), result.getName());
        verify(repository, times(1)).findByNameProduct("Queijo");
    }

    @Test
    @DisplayName("should throw NotFoundException when finding a product by name that does not exist")
    void findByName_ShouldThrowNotFoundException_WhenProductDoesNotExist() {
        // Arrange
        when(repository.findByNameProduct("Pão")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.findByName("Pão"));
        verify(repository, times(1)).findByNameProduct("Pão");
    }

    @Test
    @DisplayName("should update a product successfully with a new name")
    void update_ShouldUpdateProduct_WhenProductExistsAndNameIsNew() {
        // Arrange
        when(repository.findByIdProduct(1L)).thenReturn(Optional.of(product));
        when(repository.findByNameProduct(updatedProductRequestDTO.getName())).thenReturn(Optional.empty());
        doNothing().when(repository).updateProduct(anyLong(), anyString());

        // Act
        ProductResponseDTO result = service.update(1L, updatedProductRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(updatedProductResponseDTO.getName(), result.getName());
        verify(repository, times(1)).findByIdProduct(1L);
        verify(repository, times(1)).findByNameProduct(updatedProductRequestDTO.getName());
        verify(repository, times(1)).updateProduct(1L, updatedProductRequestDTO.getName());
    }

    @Test
    @DisplayName("should throw NotFoundException when trying to update a product that does not exist")
    void update_ShouldThrowNotFoundException_WhenProductDoesNotExist() {
        // Arrange
        when(repository.findByIdProduct(2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.update(2L, productRequestDTO));
        verify(repository, times(1)).findByIdProduct(2L);
        verify(repository, never()).updateProduct(anyLong(), anyString());
    }

    @Test
    @DisplayName("should throw BadRequestException when trying to update a product with an existing name")
    void update_ShouldThrowBadRequestException_WhenNewNameAlreadyExists() {
        // Arrange
        Product existingProductWithOtherName = new Product(2L, "Pão");

        when(repository.findByIdProduct(1L)).thenReturn(Optional.of(product));
        when(repository.findByNameProduct("Pão")).thenReturn(Optional.of(existingProductWithOtherName));

        ProductRequestDTO updateDto = new ProductRequestDTO("Pão");

        // Act & Assert
        assertThrows(BadRequestException.class, () -> service.update(1L, updateDto));
        verify(repository, times(1)).findByIdProduct(1L);
        verify(repository, times(1)).findByNameProduct("Pão");
        verify(repository, never()).updateProduct(anyLong(), anyString());
    }

    @Test
    @DisplayName("should delete a product successfully")
    void delete_ShouldDeleteProduct_WhenProductExists() {
        // Arrange
        when(repository.findByIdProduct(1L)).thenReturn(Optional.of(product));
        doNothing().when(repository).deleteProduct(1L);

        // Act
        service.delete(1L);

        // Assert
        verify(repository, times(1)).findByIdProduct(1L);
        verify(repository, times(1)).deleteProduct(1L);
    }

    @Test
    @DisplayName("should throw NotFoundException when trying to delete a product that does not exist")
    void delete_ShouldThrowNotFoundException_WhenProductDoesNotExist() {
        // Arrange
        when(repository.findByIdProduct(2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.delete(2L));
        verify(repository, times(1)).findByIdProduct(2L);
        verify(repository, never()).deleteProduct(anyLong());
    }

}
