package com.sulwork.breakfast.persistence.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sulwork.breakfast.persistence.model.Breakfast;

import jakarta.transaction.Transactional;

@Repository
public interface BreakfastRepository extends JpaRepository<Breakfast, Long> {

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO breakfast (date) VALUES (:date)", nativeQuery = true)
    void createBreakfast(LocalDate date);

    @Query(value = "SELECT * FROM breakfast", nativeQuery = true)
    List<Breakfast> findAllBreakfasts();

    @Query(value = "SELECT * FROM breakfast WHERE id = :id", nativeQuery = true)
    Optional<Breakfast> findByIdBreakfast(Long id);

    @Query(value = "SELECT * FROM breakfast WHERE date = :date", nativeQuery = true)
    Optional<Breakfast> findByDateBreakfast(LocalDate date);

    @Transactional
    @Modifying
    @Query(value = "UPDATE breakfast SET date = :date WHERE id = :id", nativeQuery = true)
    void updateBreakfast(Long id, LocalDate date);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM breakfast WHERE id = :id", nativeQuery = true)
    void deleteBreakfast(Long id);

}
