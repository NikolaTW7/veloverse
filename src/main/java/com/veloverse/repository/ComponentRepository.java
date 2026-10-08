package com.veloverse.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.veloverse.model.Component;

public interface ComponentRepository extends JpaRepository<Component, Integer> {

    // Loads the component together with its system and materials for the detail page
    @EntityGraph(attributePaths = {"system", "materials"})
    Optional<Component> findWithDetailsById(Integer id);

    List<Component> findBySystemIdOrderByIdAsc(Integer systemId);
}
