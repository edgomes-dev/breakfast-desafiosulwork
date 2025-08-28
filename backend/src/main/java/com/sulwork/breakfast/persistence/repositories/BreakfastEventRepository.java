package com.sulwork.breakfast.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sulwork.breakfast.persistence.model.BreakfastEvent;

import jakarta.transaction.Transactional;

public interface BreakfastEventRepository extends JpaRepository<BreakfastEvent, Long> {

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO breakfast_items (breakfast_id, user_id, product_id, delivered)
            VALUES (:breakfastId, :userId, :productId, false)
            """, nativeQuery = true)
    void chooseItem(@Param("breakfastId") Long breakfastId,
            @Param("userId") Long userId,
            @Param("productId") Long productId);

    // 2. Usuário exclui item (DELETE)
    @Modifying
    @Transactional
    @Query(value = """
            DELETE FROM breakfast_items
            WHERE breakfast_id = :breakfastId
              AND user_id = :userId
              AND product_id = :productId
            """, nativeQuery = true)
    void removeItem(@Param("breakfastId") Long breakfastId,
            @Param("userId") Long userId,
            @Param("productId") Long productId);

    // 3. Admin confirma entrega (UPDATE delivered)
    @Modifying
    @Transactional
    @Query(value = """
            UPDATE breakfast_items
            SET delivered = :delivered
            WHERE id = :id
            """, nativeQuery = true)
    void updateDelivered(@Param("id") Long id,
            @Param("delivered") Boolean delivered);

}
