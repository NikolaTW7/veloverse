package com.veloverse.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.veloverse.model.BicycleType;
import com.veloverse.repository.BicycleTypeRepository;

/**
 * Application logic for bicycle types, used by BicycleTypeController and HomeController.
 */
// @Service: Spring creates one shared object of this class.
// @Transactional(readOnly = true): each public method runs in a read-only database transaction.
@Service
@Transactional(readOnly = true)
public class BicycleTypeService {

    // Set once by the constructor (constructor injection) and never changed
    private final BicycleTypeRepository repository;

    public BicycleTypeService(BicycleTypeRepository repository) {
        this.repository = repository;
    }

    /** All bicycle types in ID order. */
    public List<BicycleType> findAll() {
        return repository.findAllByOrderByIdAsc();
    }

    /** One type, or an empty Optional if no type has this ID. */
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
