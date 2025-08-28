package com.sulwork.breakfast.services.impls;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sulwork.breakfast.dtos.BreakfastRequestDTO;
import com.sulwork.breakfast.dtos.BreakfastResponseDTO;
import com.sulwork.breakfast.dtos.mappers.BreakfastMapper;
import com.sulwork.breakfast.persistence.model.Breakfast;
import com.sulwork.breakfast.persistence.repositories.BreakfastRepository;
import com.sulwork.breakfast.services.BreakfastService;
import com.sulwork.breakfast.services.exceptions.BadRequestException;
import com.sulwork.breakfast.services.exceptions.NotFoundException;

@Service
public class BreakfastServiceImpl implements BreakfastService {

    @Autowired
    BreakfastRepository repository;

    @Override
    public BreakfastResponseDTO create(BreakfastRequestDTO dto) {

        repository.createBreakfast(dto.getDate());

        Breakfast savedBreakfast = repository.findByDateBreakfast(dto.getDate())
                .orElseThrow(() -> new RuntimeException("Falha ao recuperar evento"));

        return BreakfastMapper.toDto(savedBreakfast);

    }

    @Override
    public List<BreakfastResponseDTO> findAll() {

        List<Breakfast> list = repository.findAllBreakfasts();

        return list.stream()
                .map(BreakfastMapper::toDto)
                .collect(Collectors.toList());

    }

    @Override
    public BreakfastResponseDTO findById(Long id) {

        Breakfast breakfast = repository.findByIdBreakfast(id)
                .orElseThrow(() -> new NotFoundException("Evento não encontrado!"));

        return BreakfastMapper.toDto(breakfast);

    }

    @Override
    public BreakfastResponseDTO findByDate(LocalDate date) {

        Breakfast breakfast = repository.findByDateBreakfast(date)
                .orElseThrow(() -> new NotFoundException("Evento não encontrado!"));

        return BreakfastMapper.toDto(breakfast);

    }

    @Override
    public BreakfastResponseDTO update(Long id, BreakfastRequestDTO dto) {

        Breakfast existBreakfast = repository.findByIdBreakfast(id)
                .orElseThrow(() -> new NotFoundException("Evento não encontrado"));

        if (existBreakfast.getDate() != dto.getDate()) {
            repository.findByDateBreakfast(dto.getDate())
                    .ifPresent(user -> {
                        throw new BadRequestException("Evento para esta data, já criado!");
                    });
        }

        repository.updateBreakfast(id, dto.getDate());

        Breakfast breakfast = repository.findByIdBreakfast(id)
                .orElseThrow(() -> new NotFoundException("Falha ao recuperar evento!"));

        return BreakfastMapper.toDto(breakfast);

    }

    @Override
    public void delete(Long id) {

        if (repository.findByIdBreakfast(id).isEmpty()) {
            throw new NotFoundException("Evento não encontrado!");
        }

        repository.deleteBreakfast(id);

    }

}
