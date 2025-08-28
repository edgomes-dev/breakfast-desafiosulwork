package com.sulwork.breakfast.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.sulwork.breakfast.dtos.ProductRequestDTO;
import com.sulwork.breakfast.dtos.ProductResponseDTO;
import com.sulwork.breakfast.persistence.model.Product;
import com.sulwork.breakfast.persistence.repositories.ProductRepository;
import com.sulwork.breakfast.services.exceptions.BadRequestException;
import com.sulwork.breakfast.services.exceptions.NotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ProductServiceIntegrationTest {

    @Autowired
    private ProductService service;

    @Autowired
    private ProductRepository repository;

    private Product product;
    private ProductRequestDTO dto;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        // Arrange
        product = new Product(null, "Queijo");
        repository.createProduct(product.getName());
        dto = new ProductRequestDTO("presunto");
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("should create a new Product in the database")
    void create_ShouldSaveProduct_InDatabase() {
        // Act
        ProductResponseDTO createdProduct = service.create(dto);

        // Assert
        assertNotNull(createdProduct);
        assertEquals(dto.getName(), createdProduct.getName());

        Optional<Product> foundProduct = repository.findByNameProduct(createdProduct.getName());
        assertTrue(foundProduct.isPresent());
        assertEquals(dto.getName(), foundProduct.get().getName());
    }

    @Test
    @DisplayName("should throw BadRequestException when creating a product with an existing name")
    void create_ShouldThrowBadRequestException_WhenNameAlreadyExists() {
        // Arrange
        ProductRequestDTO existingNameDto = new ProductRequestDTO("Queijo");

        // Act & Assert
        assertThrows(BadRequestException.class, () -> service.create(existingNameDto));
    }

    @Test
    @DisplayName("should find all products from the database")
    void findAll_ShouldReturnAllProducts() {
        // Act
        List<ProductResponseDTO> products = service.findAll();

        // Assert
        assertNotNull(products);
        assertFalse(products.isEmpty());
        assertEquals(1, products.size());
    }

    @Test
    @DisplayName("should find a product by ID from the database")
    void findById_ShouldReturnProduct_WhenIdExists() {
        // Arrange
        Product savedProduct = repository.findByNameProduct("Queijo").orElseThrow();

        // Act
        ProductResponseDTO foundProduct = service.findById(savedProduct.getId());

        // Assert
        assertNotNull(foundProduct);
        assertEquals(savedProduct.getId(), foundProduct.getId());
        assertEquals(savedProduct.getName(), foundProduct.getName());
    }

    @Test
    @DisplayName("should throw NotFoundException when finding a product by a non-existent ID")
    void findById_ShouldThrowNotFoundException_WhenIdDoesNotExist() {
        // Arrange
        Long nonExistentId = 99L;

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.findById(nonExistentId));
    }

    @Test
    @DisplayName("should find a product by name from the database")
    void findByName_ShouldReturnProduct_WhenNameExists() {
        // Arrange
        String existingName = "Queijo";

        // Act
        ProductResponseDTO foundProduct = service.findByName(existingName);

        // Assert
        assertNotNull(foundProduct);
        assertEquals(existingName, foundProduct.getName());
    }

    @Test
    @DisplayName("should throw NotFoundException when finding a product by a non-existent name")
    void findByName_ShouldThrowNotFoundException_WhenNameDoesNotExist() {
        // Arrange
        String nonExistentName = "Pão";

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.findByName(nonExistentName));
    }

    @Test
    @DisplayName("should update an existing product in the database")
    void update_ShouldUpdateProduct_WhenIdExists() {
        // Arrange
        Product savedProduct = repository.findByNameProduct("Queijo").orElseThrow();
        String newName = "Queijo Minas";
        ProductRequestDTO updateDto = new ProductRequestDTO(newName);

        // Act
        ProductResponseDTO updatedProduct = service.update(savedProduct.getId(), updateDto);

        // Assert
        assertNotNull(updatedProduct);
        assertEquals(newName, updatedProduct.getName());

        Optional<Product> foundProduct = repository.findByIdProduct(savedProduct.getId());
        assertTrue(foundProduct.isPresent());
        assertEquals(newName, foundProduct.get().getName());
    }

    @Test
    @DisplayName("should delete an existing product from the database")
    void delete_ShouldRemoveProduct_WhenIdExists() {
        // Arrange
        Product savedProduct = repository.findByNameProduct("Queijo").orElseThrow();
        Long productId = savedProduct.getId();

        // Act
        service.delete(productId);

        // Assert
        Optional<Product> deletedProduct = repository.findByIdProduct(productId);
        assertFalse(deletedProduct.isPresent());
    }

    @Test
    @DisplayName("should throw NotFoundException when trying to delete a non-existent product")
    void delete_ShouldThrowNotFoundException_WhenIdDoesNotExist() {
        // Arrange
        Long nonExistentId = 99L;

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.delete(nonExistentId));
    }
}