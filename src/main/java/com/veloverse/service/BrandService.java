package com.veloverse.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.veloverse.model.Brand;
import com.veloverse.repository.BrandRepository;

/**
 * Application logic for brands, used by BrandController.
 */
// @Service: Spring creates one shared object of this class.
// @Transactional(readOnly = true): each public method runs in a read-only database transaction.
@Service
@Transactional(readOnly = true)
public class BrandService {

    // Set once by the constructor (constructor injection) and never changed
    private final BrandRepository repository;

    public BrandService(BrandRepository repository) {
        this.repository = repository;
    }

    /** All brands in a stable display order (by ID). */
    // Sort.by("id") sorts on the id field of the Brand entity
    public List<Brand> findAll() {
        return repository.findAll(Sort.by("id"));
    }

    /** One brand, or an empty Optional if no brand has this ID. */
    public Optional<Brand> findById(Integer id) {
        return repository.findById(id);
    }
}
