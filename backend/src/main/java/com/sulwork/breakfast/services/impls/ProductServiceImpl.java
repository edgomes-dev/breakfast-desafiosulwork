package com.sulwork.breakfast.services.impls;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sulwork.breakfast.dtos.ProductRequestDTO;
import com.sulwork.breakfast.dtos.ProductResponseDTO;
import com.sulwork.breakfast.dtos.mappers.ProductMapper;
import com.sulwork.breakfast.persistence.model.Product;
import com.sulwork.breakfast.persistence.repositories.ProductRepository;
import com.sulwork.breakfast.services.ProductService;
import com.sulwork.breakfast.services.exceptions.BadRequestException;
import com.sulwork.breakfast.services.exceptions.NotFoundException;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository repository;

    @Override
    public ProductResponseDTO create(ProductRequestDTO dto) {

        repository.findByNameProduct(dto.getName())
                .ifPresent(product -> {
                    throw new BadRequestException("Este produto já existe!");
                });

        repository.createProduct(dto.getName());

        Product savedProduct = repository.findByNameProduct(dto.getName())
                .orElseThrow(() -> new RuntimeException("Falha ao recuperar produto"));

        return ProductMapper.toDto(savedProduct);

    }

    @Override
    public List<ProductResponseDTO> findAll() {

        List<Product> list = repository.findAllProducts();

        return list.stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());

    }

    @Override
    public ProductResponseDTO findById(Long id) {

        Product product = repository.findByIdProduct(id)
                .orElseThrow(() -> new NotFoundException("Produto não encontrado!"));

        return ProductMapper.toDto(product);

    }

    @Override
    public ProductResponseDTO update(Long id, ProductRequestDTO dto) {

        Product existProduct = repository.findByIdProduct(id)
                .orElseThrow(() -> new NotFoundException("Produto não encontrado"));

        if (existProduct.getName() != dto.getName()) {
            repository.findByNameProduct(dto.getName())
                    .ifPresent(user -> {
                        throw new BadRequestException("Produto já existe, com esse nome!");
                    });
        }

        repository.updateProduct(id, dto.getName());

        Product product = repository.findByIdProduct(id)
                .orElseThrow(() -> new NotFoundException("Falha ao recuperar usuário!"));

        return ProductMapper.toDto(product);

    }

    @Override
    public void delete(Long id) {

        if (repository.findByIdProduct(id).isEmpty()) {
            throw new NotFoundException("Usuário não encontrado!");
        }

        repository.deleteProduct(id);

    }

}
