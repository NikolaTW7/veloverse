package com.veloverse.repository;

import java.util.List;
import java.util.Optional;

// Spring Data JPA: the base repository interface and the annotation for loading related data
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.veloverse.model.Component;

/**
 * Database access for components.
 * Spring Data JPA creates the implementation automatically and builds each query from the method name.
 */
public interface ComponentRepository extends JpaRepository<Component, Integer> {

    // Loads the component together with its system and materials for the detail page
    // (both are lazy in Component, so @EntityGraph is needed to have them ready for the template).
    @EntityGraph(attributePaths = {"system", "materials"})
    Optional<Component> findWithDetailsById(Integer id);

    // All components of one system, in ID order (only their own fields are used, so no @EntityGraph)
    List<Component> findBySystemIdOrderByIdAsc(Integer systemId);
}
