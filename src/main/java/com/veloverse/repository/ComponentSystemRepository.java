package com.veloverse.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.veloverse.model.ComponentSystem;

public interface ComponentSystemRepository extends JpaRepository<ComponentSystem, Integer> {

    // Loads each system together with its components in one query
    @EntityGraph(attributePaths = "components")
    List<ComponentSystem> findAllByOrderByIdAsc();
}
