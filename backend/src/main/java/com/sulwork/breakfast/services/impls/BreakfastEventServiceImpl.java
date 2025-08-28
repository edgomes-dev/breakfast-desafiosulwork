package com.sulwork.breakfast.services.impls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sulwork.breakfast.persistence.repositories.BreakfastEventRepository;
import com.sulwork.breakfast.services.BreakfastEventService;

@Service
public class BreakfastEventServiceImpl implements BreakfastEventService {

    @Autowired
    private BreakfastEventRepository repository;

    @Override
    public void chooseItem(Long breakfastId, Long userId, Long productId) {
        repository.chooseItem(breakfastId, userId, productId);
    }

    @Override
    public void removeItem(Long userId, Long breakfastId, Long productId) {
        repository.removeItem(breakfastId, userId, productId);
    }

    @Override
    public void confirmDelivered(Long breakfastEventId, boolean delivered) {
        repository.updateDelivered(breakfastEventId, delivered);
    }

}