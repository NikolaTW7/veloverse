package com.veloverse.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.veloverse.model.Material;
import com.veloverse.repository.MaterialRepository;

/**
 * Application logic for materials, used by MaterialController.
 */
// @Service: Spring creates one shared object of this class.
// @Transactional(readOnly = true): each public method runs in a read-only database transaction.
@Service
@Transactional(readOnly = true)
public class MaterialService {

    // Set once by the constructor (constructor injection) and never changed
    private final MaterialRepository repository;

    public MaterialService(MaterialRepository repository) {
        this.repository = repository;
    }

    /** All materials in a stable display order (by ID). */
    // Sort.by("id") sorts on the id field of the Material entity
    public List<Material> findAll() {
        return repository.findAll(Sort.by("id"));
    }

    /** One material, or an empty Optional if no material has this ID. */
    public Optional<Material> findById(Integer id) {
        return repository.findById(id);
    }
}
