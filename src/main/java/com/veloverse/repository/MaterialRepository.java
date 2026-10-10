package com.veloverse.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.veloverse.model.Material;

/**
 * Database access for materials.
 * The body is empty because the methods inherited from JpaRepository (findAll, findById ...)
 * are all that MaterialService needs. Spring Data JPA creates the implementation automatically.
 */
public interface MaterialRepository extends JpaRepository<Material, Integer> {
}
