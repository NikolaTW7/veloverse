package com.veloverse.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.veloverse.model.Brand;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
}
