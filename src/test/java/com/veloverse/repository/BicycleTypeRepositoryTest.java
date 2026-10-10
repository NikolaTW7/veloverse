package com.veloverse.repository;

// Static import: lets the tests write assertThat(...) (from AssertJ) instead of Assertions.assertThat(...)
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.veloverse.model.BicycleType;

/**
 * Checks against the real PostgreSQL database that schema.sql and data.sql ran
 * and that the entity maps the bicycle_types columns correctly.
 * Like the existing startup test, this needs DB_PASSWORD to be set. It only reads data.
 * @SpringBootTest starts the whole application against the separate veloverse_test database
 * (see src/test/resources/config/application.properties), so nothing is mocked here.
 */
@SpringBootTest
class BicycleTypeRepositoryTest {

    // @Autowired: Spring puts its real repository object into this field before the tests run
    @Autowired
    private BicycleTypeRepository repository;

    // contains (not containsExactly) allows extra types; isSorted checks the IDs come back in ascending order
    @Test
    void starterBicycleTypesAreLoadedInOrder() {
        List<BicycleType> types = repository.findAllByOrderByIdAsc();

        assertThat(types).extracting(BicycleType::getName)
                .contains("Road Bike", "Mountain Bike", "Gravel Bike", "Hybrid Bike",
                        "BMX", "Fixed-Gear Bike", "Touring Bike", "Electric Bike");
        assertThat(types).extracting(BicycleType::getId).isSorted();
    }

    // The advantages and disadvantages text columns are split into lists (one item per line) by the entity
    @Test
    void columnsAndAdvantageListsAreMapped() {
        BicycleType road = repository.findAllByOrderByIdAsc().stream()
                .filter(type -> type.getName().equals("Road Bike"))
                .findFirst()
                .orElseThrow();

        assertThat(road.getDescription()).isNotBlank();
        assertThat(road.getBestUse()).isNotBlank();
        assertThat(road.getAdvantageList()).hasSize(3);
        assertThat(road.getDisadvantageList()).hasSize(3);
    }

    @Test
    void timeTrialTriathlonTypeIsLoadedWithItsDetails() {
        BicycleType tt = repository.findAllByOrderByIdAsc().stream()
                .filter(type -> type.getName().equals("Time-Trial / Triathlon Bike"))
                .findFirst()
                .orElseThrow();

        assertThat(tt.getDescription()).contains("aero extensions");
        assertThat(tt.getBestUse()).isNotBlank();
        assertThat(tt.getAdvantageList()).isNotEmpty();
        assertThat(tt.getDisadvantageList()).isNotEmpty();
    }
}
