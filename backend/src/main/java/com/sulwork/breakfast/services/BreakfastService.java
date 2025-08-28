package com.sulwork.breakfast.services;

import java.time.LocalDate;
import java.util.List;

import com.sulwork.breakfast.dtos.BreakfastRequestDTO;
import com.sulwork.breakfast.dtos.BreakfastResponseDTO;

public interface BreakfastService {

    BreakfastResponseDTO create(BreakfastRequestDTO dto);

    List<BreakfastResponseDTO> findAll();

    BreakfastResponseDTO findById(Long id);

    BreakfastResponseDTO findByDate(LocalDate date);

    BreakfastResponseDTO update(Long id, BreakfastRequestDTO dto);

    void delete(Long id);

}
