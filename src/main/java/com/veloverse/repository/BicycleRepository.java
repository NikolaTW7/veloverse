package com.veloverse.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.veloverse.model.Bicycle;
import com.veloverse.model.BicycleType;
import com.veloverse.model.Brand;
import com.veloverse.model.Material;

public interface BicycleRepository extends JpaRepository<Bicycle, Integer> {

    // Loads each bicycle together with its brand, type and frame material in one query
    @EntityGraph(attributePaths = {"brand", "type", "frameMaterial"})
    List<Bicycle> findAllByOrderByIdAsc();

    // One bicycle with its brand, type and frame material loaded, for the detail page
    @EntityGraph(attributePaths = {"brand", "type", "frameMaterial"})
    Optional<Bicycle> findWithDetailsById(Integer id);

    // All bicycles from one brand, with type and frame material loaded, for the Brand Detail page
    @EntityGraph(attributePaths = {"brand", "type", "frameMaterial"})
    List<Bicycle> findByBrandIdOrderByIdAsc(Integer brandId);

    // Options for the filter dropdowns: only the types, brands and frame materials that at least
    // one bicycle uses, in alphabetical order. (The materials table also holds component
    // materials such as Rubber, which would make no sense as a frame material filter.)
    @Query("SELECT DISTINCT t FROM Bicycle b JOIN b.type t ORDER BY t.name")
    List<BicycleType> findTypesInUse();

    @Query("SELECT DISTINCT br FROM Bicycle b JOIN b.brand br ORDER BY br.name")
    List<Brand> findBrandsInUse();

    @Query("SELECT DISTINCT m FROM Bicycle b JOIN b.frameMaterial m ORDER BY m.name")
    List<Material> findFrameMaterialsInUse();
}
