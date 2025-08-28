package com.sulwork.breakfast.resources;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sulwork.breakfast.dtos.BreakfastRequestDTO;
import com.sulwork.breakfast.dtos.BreakfastResponseDTO;
import com.sulwork.breakfast.services.BreakfastService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/breakfasts")
public class BreakfastController {

    @Autowired
    private BreakfastService service;

    @PostMapping
    public ResponseEntity<BreakfastResponseDTO> create(@RequestBody BreakfastRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<BreakfastResponseDTO>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BreakfastResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(service.findById(id));
    }

    @GetMapping("/dates/{date}")
    public ResponseEntity<BreakfastResponseDTO> findByDate(@PathVariable LocalDate date) {
        return ResponseEntity.status(HttpStatus.OK).body(service.findByDate(date));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BreakfastResponseDTO> update(@PathVariable Long id, @RequestBody BreakfastRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        service.delete(id);

        return ResponseEntity.status(HttpStatus.OK).body("Sucess!");
    }

}
