package com.veloverse.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.veloverse.model.Material;

public interface MaterialRepository extends JpaRepository<Material, Integer> {
}
