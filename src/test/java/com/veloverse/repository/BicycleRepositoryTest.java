package com.veloverse.repository;

// Static import: lets the tests write assertThat(...) (from AssertJ) instead of Assertions.assertThat(...)
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.veloverse.model.Bicycle;
import com.veloverse.model.BicycleType;
import com.veloverse.model.Brand;
import com.veloverse.model.Material;
import com.veloverse.service.BicycleService;

/**
 * Checks against the real PostgreSQL database that the six bicycle models are loaded,
 * and that each is linked to the correct brand, type and (when confirmed) frame material.
 * Like the other database tests, this needs DB_PASSWORD to be set. It runs against the separate
 * veloverse_test database (see src/test/resources/config/application.properties), which is
 * rebuilt from schema.sql and data.sql for every test run, so no update script is needed.
 * This is an integration test: @SpringBootTest starts the whole application, so the real repository
 * runs real SQL. Nothing is mocked, so the expected values below come from data.sql.
 */
@SpringBootTest
class BicycleRepositoryTest {

    // @Autowired: Spring puts its real repository and service objects into these fields before the tests run
    @Autowired
    private BicycleRepository repository;

    @Autowired
    private BicycleService service;

    /** Picks the bicycle with this model name from a list, or throws (failing the test) if it is not there. */
    private Bicycle find(List<Bicycle> bicycles, String name) {
        return bicycles.stream()
                .filter(bicycle -> bicycle.getName().equals(name))
                .findFirst()
                .orElseThrow();
    }

    @Test
    void exactlySixBicyclesAreLoadedInOrder() {
        // Act: read every bicycle from the database
        List<Bicycle> bicycles = repository.findAllByOrderByIdAsc();

        // Assert: extracting(...) turns the list of bicycles into a list of names (or IDs) to compare
        assertThat(bicycles).extracting(Bicycle::getName)
                .containsExactly("S5", "Folding Bicycle", "V3-RS", "Avant", "Speedmax CFR",
                        "Endurace WMN CF SL Disc 8.0 Di2");
        assertThat(bicycles).extracting(Bicycle::getId).isSorted();
    }

    @Test
    void bothCanyonModelsBelongToOneCanyonBrand() {
        List<Bicycle> bicycles = repository.findAllByOrderByIdAsc();

        assertThat(bicycles).extracting(bike -> bike.getBrand().getName())
                .containsExactly("Cervélo", "Brompton", "Colnago", "Orbea", "Canyon", "Canyon");
        assertThat(find(bicycles, "Speedmax CFR").getBrand().getId())
                .isEqualTo(find(bicycles, "Endurace WMN CF SL Disc 8.0 Di2").getBrand().getId());
    }

    @Test
    void bicyclesAreLinkedToBrandTypeAndFrameMaterial() {
        List<Bicycle> bicycles = repository.findAllByOrderByIdAsc();

        Bicycle speedmax = find(bicycles, "Speedmax CFR");
        assertThat(speedmax.getType().getName()).isEqualTo("Time-Trial / Triathlon Bike");
        assertThat(speedmax.getFrameMaterial().getName()).isEqualTo("Carbon Fibre");
        assertThat(speedmax.getYear()).isEqualTo(2020);

        Bicycle brompton = find(bicycles, "Folding Bicycle");
        assertThat(brompton.getType().getName()).isEqualTo("Folding Bike");

        assertThat(find(bicycles, "S5").getYear()).isEqualTo(2019);
    }

    @Test
    void unconfirmedValuesAreLeftEmpty() {
        List<Bicycle> bicycles = repository.findAllByOrderByIdAsc();

        // The exact Brompton and Orbea variants are not confirmed, so their frame material is NULL
        assertThat(find(bicycles, "Folding Bicycle").getFrameMaterial()).isNull();
        assertThat(find(bicycles, "Avant").getFrameMaterial()).isNull();
        assertThat(find(bicycles, "Folding Bicycle").getYear()).isNull();
        assertThat(find(bicycles, "Avant").getYear()).isNull();
        assertThat(find(bicycles, "V3-RS").getYear()).isNull();
        // No complete-bike weight was confirmed for any of the six models
        assertThat(bicycles).allSatisfy(bike -> assertThat(bike.getWeightKg()).isNull());
    }

    // allSatisfy runs the checks inside the lambda for every bicycle in the list
    @Test
    void everyModelHasEducationalDetailsAndASource() {
        assertThat(repository.findAllByOrderByIdAsc()).allSatisfy(bike -> {
            assertThat(bike.getDescription()).isNotBlank();
            assertThat(bike.getIntendedUse()).isNotBlank();
            assertThat(bike.getFeatureList()).isNotEmpty();
            assertThat(bike.getAdvantageList()).isNotEmpty();
            assertThat(bike.getDisadvantageList()).isNotEmpty();
            assertThat(bike.getSourceUrl()).startsWith("https://");
        });
    }

    // The ID is looked up first because IDs come from the database and are not hard-coded; -1 is never a real ID
    @Test
    void findWithDetailsByIdLoadsOneBicycleOrNothing() {
        Bicycle avant = find(repository.findAllByOrderByIdAsc(), "Avant");

        Bicycle found = repository.findWithDetailsById(avant.getId()).orElseThrow();
        assertThat(found.getName()).isEqualTo("Avant");
        assertThat(found.getBrand().getName()).isEqualTo("Orbea");
        assertThat(found.getType().getName()).isEqualTo("Road Bike");
        assertThat(found.getFrameMaterial()).isNull();

        assertThat(repository.findWithDetailsById(-1)).isEmpty();
    }

    @Test
    void findByBrandReturnsOnlyThatBrandsBicycles() {
        Bicycle speedmax = find(repository.findAllByOrderByIdAsc(), "Speedmax CFR");

        List<Bicycle> canyonBicycles = repository.findByBrandIdOrderByIdAsc(speedmax.getBrand().getId());
        assertThat(canyonBicycles).extracting(Bicycle::getName)
                .containsExactly("Speedmax CFR", "Endurace WMN CF SL Disc 8.0 Di2");

        assertThat(repository.findByBrandIdOrderByIdAsc(-1)).isEmpty();
    }

    // Same search rules as BicycleServiceTest, but on the real data: accents and case are ignored
    @Test
    void searchWorksWithTheRealData() {
        assertThat(service.search("cervelo")).extracting(Bicycle::getName).containsExactly("S5");
        assertThat(service.search("canyon")).extracting(Bicycle::getName)
                .containsExactly("Speedmax CFR", "Endurace WMN CF SL Disc 8.0 Di2");
        assertThat(service.search("triathlon")).extracting(Bicycle::getName).containsExactly("Speedmax CFR");
        assertThat(service.search("folding")).extracting(Bicycle::getName).containsExactly("Folding Bicycle");
        assertThat(service.search("road bike")).extracting(Bicycle::getName)
                .containsExactly("S5", "V3-RS", "Avant", "Endurace WMN CF SL Disc 8.0 Di2");
    }

    // The filter dropdowns list only types, brands and materials that at least one bicycle uses
    @Test
    void filterOptionsOnlyIncludeValuesUsedByBicycles() {
        assertThat(repository.findTypesInUse()).extracting(BicycleType::getName)
                .containsExactlyInAnyOrder("Folding Bike", "Road Bike", "Time-Trial / Triathlon Bike");
        assertThat(repository.findBrandsInUse()).extracting(Brand::getName)
                .containsExactlyInAnyOrder("Brompton", "Canyon", "Cervélo", "Colnago", "Orbea");
        // Bicycles without a confirmed frame material do not add an empty option
        assertThat(repository.findFrameMaterialsInUse()).extracting(Material::getName)
                .containsExactly("Carbon Fibre");
    }

    @Test
    void materialFilterSkipsBicyclesWithoutAConfirmedFrameMaterial() {
        // Carbon Fibre is the only material in use (checked in the test above), so it is the first item
        Material carbon = repository.findFrameMaterialsInUse().get(0);

        assertThat(service.search(null, null, null, carbon.getId())).extracting(Bicycle::getName)
                .containsExactly("S5", "V3-RS", "Speedmax CFR", "Endurace WMN CF SL Disc 8.0 Di2");
    }
}
