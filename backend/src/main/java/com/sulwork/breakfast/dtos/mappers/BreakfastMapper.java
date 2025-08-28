package com.sulwork.breakfast.dtos.mappers;

import com.sulwork.breakfast.dtos.BreakfastRequestDTO;
import com.sulwork.breakfast.dtos.BreakfastResponseDTO;
import com.sulwork.breakfast.persistence.model.Breakfast;

public class BreakfastMapper {

    public static Breakfast toDomain(BreakfastRequestDTO dto) {
        return Breakfast.builder()
                .date(dto.getDate())
                .build();
    }

    public static BreakfastResponseDTO toDto(Breakfast breakfast) {
        return BreakfastResponseDTO.builder()
                .id(breakfast.getId())
                .date(breakfast.getDate())
                .build();
    }

}
