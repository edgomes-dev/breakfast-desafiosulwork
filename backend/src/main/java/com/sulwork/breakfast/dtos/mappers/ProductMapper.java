package com.sulwork.breakfast.dtos.mappers;

import com.sulwork.breakfast.dtos.ProductRequestDTO;
import com.sulwork.breakfast.dtos.ProductResponseDTO;
import com.sulwork.breakfast.persistence.model.Product;

public class ProductMapper {

    public static Product toDomain(ProductRequestDTO dto) {
        return Product.builder()
                .name(dto.getName())
                .build();
    }

    public static ProductResponseDTO toDto(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .build();
    }

}
