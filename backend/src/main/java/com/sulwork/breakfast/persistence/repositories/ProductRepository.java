package com.sulwork.breakfast.persistence.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sulwork.breakfast.persistence.model.Product;

import jakarta.transaction.Transactional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO products (name) VALUES (:name)", nativeQuery = true)
    void createProduct(String name);

    @Query(value = "SELECT * FROM products", nativeQuery = true)
    List<Product> findAllProducts();

    @Query(value = "SELECT * FROM products WHERE id = :id", nativeQuery = true)
    Optional<Product> findByIdProduct(Long id);

    @Query(value = "SELECT * FROM products WHERE name = :name", nativeQuery = true)
    Optional<Product> findByNameProduct(String name);

    @Transactional
    @Modifying
    @Query(value = "UPDATE products SET name = :name WHERE id = :id", nativeQuery = true)
    void updateProduct(Long id, String name);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM products WHERE id = :id", nativeQuery = true)
    void deleteProduct(Long id);

}
