package com.veloverse.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.veloverse.model.BicycleType;

public interface BicycleTypeRepository extends JpaRepository<BicycleType, Integer> {

    List<BicycleType> findAllByOrderByIdAsc();
}
