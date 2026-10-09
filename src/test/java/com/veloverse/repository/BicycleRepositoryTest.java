package com.veloverse.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.veloverse.model.Bicycle;
import com.veloverse.model.BicycleType;
import com.veloverse.model.Brand;
import com.veloverse.model.Material;

/**
 * Checks against the real PostgreSQL database that the bicycles table was created and filled,
 * and that each bicycle is linked to the correct brand, type and frame material.
 * Like the other database tests, this needs DB_PASSWORD to be set. It only reads data.
 */
@SpringBootTest
class BicycleRepositoryTest {

    @Autowired
    private BicycleRepository repository;

    private Bicycle find(List<Bicycle> bicycles, String name) {
        return bicycles.stream()
                .filter(bicycle -> bicycle.getName().equals(name))
                .findFirst()
                .orElseThrow();
    }

    @Test
    void sixStarterBicyclesAreLoadedInOrder() {
        List<Bicycle> bicycles = repository.findAllByOrderByIdAsc();

        assertThat(bicycles).extracting(Bicycle::getName)
                .contains("520 Disc", "Rockhopper Sport 29", "Escape 3",
                        "Topstone 3", "C Line Explore", "Domane SL 5 Gen 4");
        assertThat(bicycles).extracting(Bicycle::getId).isSorted();
    }

    @Test
    void bicyclesAreLinkedToBrandTypeAndFrameMaterial() {
        List<Bicycle> bicycles = repository.findAllByOrderByIdAsc();

        Bicycle brompton = find(bicycles, "C Line Explore");
        assertThat(brompton.getBrand().getName()).isEqualTo("Brompton");
        assertThat(brompton.getType().getName()).isEqualTo("Folding Bike");
        assertThat(brompton.getFrameMaterial().getName()).isEqualTo("Steel");

        Bicycle domane = find(bicycles, "Domane SL 5 Gen 4");
        assertThat(domane.getBrand().getName()).isEqualTo("Trek");
        assertThat(domane.getType().getName()).isEqualTo("Road Bike");
        assertThat(domane.getFrameMaterial().getName()).isEqualTo("Carbon Fibre");
    }

    @Test
    void uncertainWeightsAreLeftEmpty() {
        List<Bicycle> bicycles = repository.findAllByOrderByIdAsc();

        assertThat(find(bicycles, "Domane SL 5 Gen 4").getWeightKg()).isEqualByComparingTo(new BigDecimal("8.93"));
        assertThat(find(bicycles, "520 Disc").getWeightKg()).isNull();
        assertThat(find(bicycles, "C Line Explore").getWeightKg()).isNull();
    }

    @Test
    void findWithDetailsByIdLoadsOneBicycleOrNothing() {
        Bicycle domane = find(repository.findAllByOrderByIdAsc(), "Domane SL 5 Gen 4");

        Bicycle found = repository.findWithDetailsById(domane.getId()).orElseThrow();
        assertThat(found.getName()).isEqualTo("Domane SL 5 Gen 4");
        assertThat(found.getBrand().getName()).isEqualTo("Trek");
        assertThat(found.getType().getName()).isEqualTo("Road Bike");
        assertThat(found.getFrameMaterial().getName()).isEqualTo("Carbon Fibre");

        assertThat(repository.findWithDetailsById(-1)).isEmpty();
    }

    @Test
    void findByBrandReturnsOnlyThatBrandsBicycles() {
        Bicycle domane = find(repository.findAllByOrderByIdAsc(), "Domane SL 5 Gen 4");
        Integer trekId = domane.getBrand().getId();

        List<Bicycle> trekBicycles = repository.findByBrandIdOrderByIdAsc(trekId);
        assertThat(trekBicycles).extracting(Bicycle::getName)
                .containsExactly("520 Disc", "Domane SL 5 Gen 4");
        assertThat(trekBicycles.get(0).getType().getName()).isEqualTo("Touring Bike");

        assertThat(repository.findByBrandIdOrderByIdAsc(-1)).isEmpty();
    }

    private List<String> searchNames(String text) {
        return repository
                .findByNameContainingIgnoreCaseOrBrandNameContainingIgnoreCaseOrTypeNameContainingIgnoreCaseOrderByIdAsc(
                        text, text, text)
                .stream().map(Bicycle::getName).toList();
    }

    @Test
    void searchMatchesModelBrandOrTypeIgnoringCase() {
        // Brand name, in any letter case
        assertThat(searchNames("trek")).containsExactly("520 Disc", "Domane SL 5 Gen 4");
        assertThat(searchNames("TREK")).containsExactly("520 Disc", "Domane SL 5 Gen 4");
        // Model name, including part of a name
        assertThat(searchNames("escape")).containsExactly("Escape 3");
        assertThat(searchNames("Rockhopper")).containsExactly("Rockhopper Sport 29");
        // Bicycle type
        assertThat(searchNames("Gravel")).containsExactly("Topstone 3");
        assertThat(searchNames("folding bike")).containsExactly("C Line Explore");
    }

    @Test
    void searchWithNoMatchReturnsNothing() {
        assertThat(searchNames("unicycle")).isEmpty();
        // % and _ are treated as normal characters, not as "match anything"
        assertThat(searchNames("%")).isEmpty();
        assertThat(searchNames("_")).isEmpty();
    }

    @Test
    void filterOptionsOnlyIncludeValuesUsedByBicyclesInAlphabeticalOrder() {
        assertThat(repository.findTypesInUse()).extracting(BicycleType::getName)
                .containsExactly("Folding Bike", "Gravel Bike", "Hybrid Bike", "Mountain Bike", "Road Bike", "Touring Bike");
        assertThat(repository.findBrandsInUse()).extracting(Brand::getName)
                .containsExactly("Brompton", "Cannondale", "Giant", "Specialized", "Trek");
        // Rubber and Titanium are in the materials table but no bicycle frame uses them
        assertThat(repository.findFrameMaterialsInUse()).extracting(Material::getName)
                .containsExactly("Aluminium", "Carbon Fibre", "Steel");
    }
}
