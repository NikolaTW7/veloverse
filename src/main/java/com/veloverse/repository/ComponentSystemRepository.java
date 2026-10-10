package com.veloverse.repository;

import java.util.List;

// Spring Data JPA: the base repository interface and the annotation for loading related data
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.veloverse.model.ComponentSystem;

/**
 * Database access for component systems.
 * Spring Data JPA creates the implementation automatically.
 */
public interface ComponentSystemRepository extends JpaRepository<ComponentSystem, Integer> {

    // Loads each system together with its components in one query
    // (the components list is lazy, so without this the overview page could not show it).
    @EntityGraph(attributePaths = "components")
    List<ComponentSystem> findAllByOrderByIdAsc();
}
