package com.veloverse.repository;

import java.util.List;
import java.util.Optional;

// Spring Data JPA: the base repository interface and annotations for custom loading and queries
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.veloverse.model.Bicycle;
import com.veloverse.model.BicycleType;
import com.veloverse.model.Brand;
import com.veloverse.model.Material;

/**
 * Database access for bicycles.
 *
 * <p>This is only an interface: Spring Data JPA writes the class that implements it when the
 * application starts. Extending JpaRepository&lt;Bicycle, Integer&gt; (entity type, ID type) gives
 * ready-made methods such as findById and findAll. Extra methods are created from their names,
 * e.g. findByBrandIdOrderByIdAsc becomes "WHERE brand_id = ? ORDER BY bicycle_id".
 *
 * <p>Brand, type and frame material are LAZY in Bicycle, so normally each would need its own
 * extra query when used. @EntityGraph lists which of them to load together with the bicycles
 * in the same query, so they are ready for the templates.
 */
public interface BicycleRepository extends JpaRepository<Bicycle, Integer> {

    // Loads each bicycle together with its brand, type and frame material in one query
    @EntityGraph(attributePaths = {"brand", "type", "frameMaterial"})
    List<Bicycle> findAllByOrderByIdAsc();

    // One bicycle with its brand, type and frame material loaded, for the detail page
    // Optional: the result may be empty when no bicycle has this ID.
    @EntityGraph(attributePaths = {"brand", "type", "frameMaterial"})
    Optional<Bicycle> findWithDetailsById(Integer id);

    // All bicycles from one brand, with type and frame material loaded, for the Brand Detail page
    @EntityGraph(attributePaths = {"brand", "type", "frameMaterial"})
    List<Bicycle> findByBrandIdOrderByIdAsc(Integer brandId);

    // Options for the filter dropdowns: only the types, brands and frame materials that at least
    // one bicycle uses, in alphabetical order. (The materials table also holds component
    // materials such as Rubber, which would make no sense as a frame material filter.)
    // @Query gives the query by hand in JPQL, which uses class and field names (Bicycle, b.type)
    // instead of table and column names. DISTINCT removes duplicates when many bicycles share a value.
    @Query("SELECT DISTINCT t FROM Bicycle b JOIN b.type t ORDER BY t.name")
    List<BicycleType> findTypesInUse();

    @Query("SELECT DISTINCT br FROM Bicycle b JOIN b.brand br ORDER BY br.name")
    List<Brand> findBrandsInUse();

    // The inner JOIN skips bicycles whose frame material is NULL (not confirmed)
    @Query("SELECT DISTINCT m FROM Bicycle b JOIN b.frameMaterial m ORDER BY m.name")
    List<Material> findFrameMaterialsInUse();
}
