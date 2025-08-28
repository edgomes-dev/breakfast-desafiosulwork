package com.sulwork.breakfast.services;

public interface BreakfastEventService {

    void chooseItem(Long breakfastId, Long userId, Long productId);

    void removeItem(Long userId, Long breakfastId, Long productId);

    void confirmDelivered(Long breakfastEventId, boolean delivered);

}
