package com.veloverse.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.veloverse.model.Material;
import com.veloverse.repository.MaterialRepository;

@Service
@Transactional(readOnly = true)
public class MaterialService {

    private final MaterialRepository repository;

    public MaterialService(MaterialRepository repository) {
        this.repository = repository;
    }

    /** All materials in a stable display order (by ID). */
    public List<Material> findAll() {
        return repository.findAll(Sort.by("id"));
    }

    /** One material, or an empty Optional if no material has this ID. */
    public Optional<Material> findById(Integer id) {
        return repository.findById(id);
    }
}
