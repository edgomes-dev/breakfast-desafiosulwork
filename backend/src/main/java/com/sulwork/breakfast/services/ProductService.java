package com.sulwork.breakfast.services;

import java.util.List;

import com.sulwork.breakfast.dtos.ProductRequestDTO;
import com.sulwork.breakfast.dtos.ProductResponseDTO;

public interface ProductService {

    ProductResponseDTO create(ProductRequestDTO dto);

    List<ProductResponseDTO> findAll();

    ProductResponseDTO findById(Long id);

    ProductResponseDTO update(Long id, ProductRequestDTO dto);

    void delete(Long id);

}
