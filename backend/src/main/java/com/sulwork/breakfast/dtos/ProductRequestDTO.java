package com.sulwork.breakfast.dtos;

import java.util.Arrays;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductRequestDTO {

    private String name;

    public ProductRequestDTO() {
        this.name = changeNameProduct(name);
    }

    public ProductRequestDTO(String name) {
        this.name = changeNameProduct(name);
    }

    String changeNameProduct(String name) {
        String text = Arrays.stream(name.split("\\s+"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));

        return text;
    }

}
