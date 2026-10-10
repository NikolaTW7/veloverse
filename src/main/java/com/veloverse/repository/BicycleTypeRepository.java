package com.veloverse.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.veloverse.model.BicycleType;

/**
 * Database access for bicycle types.
 * Spring Data JPA creates the implementation automatically; JpaRepository already provides
 * methods such as findById and findAll.
 */
public interface BicycleTypeRepository extends JpaRepository<BicycleType, Integer> {

    // All types sorted by ID. Spring builds the query from the method name.
    List<BicycleType> findAllByOrderByIdAsc();
}
