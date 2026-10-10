package com.veloverse.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.veloverse.model.Brand;

/**
 * Database access for brands.
 * The body is empty because the methods inherited from JpaRepository (findAll, findById ...)
 * are all that BrandService needs. Spring Data JPA creates the implementation automatically.
 */
public interface BrandRepository extends JpaRepository<Brand, Integer> {
}
