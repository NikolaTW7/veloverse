package com.veloverse.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.veloverse.model.BicycleType;
import com.veloverse.repository.BicycleTypeRepository;

@Service
@Transactional(readOnly = true)
public class BicycleTypeService {

    private final BicycleTypeRepository repository;

    public BicycleTypeService(BicycleTypeRepository repository) {
        this.repository = repository;
    }

    public List<BicycleType> findAll() {
        return repository.findAllByOrderByIdAsc();
    }

    public Optional<BicycleType> findById(Integer id) {
        return repository.findById(id);
    }

    /** Returns up to {@code limit} other types, used for the "Related Types" section. */
    public List<BicycleType> findOthers(BicycleType current, int limit) {
        return findAll().stream()
                .filter(type -> !type.getId().equals(current.getId()))
                .limit(limit)
                .toList();
    }
}
