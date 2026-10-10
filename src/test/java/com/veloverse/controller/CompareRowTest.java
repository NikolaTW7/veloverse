package com.veloverse.controller;

// Static import: lets the tests write assertThat(...) (from AssertJ) instead of Assertions.assertThat(...)
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.veloverse.model.Bicycle;
import com.veloverse.model.BicycleType;
import com.veloverse.model.Brand;
import com.veloverse.model.Material;

/**
 * Unit tests for the comparison rows: how values are shown and when a row counts as different.
 * These are plain JUnit tests: no Spring, no Mockito and no database, just Java objects created with "new".
 * Each test follows arrange (build two bicycles), act (CompareRow.forBicycles) and assert (check the rows).
 */
class CompareRowTest {

    /** Builds a test road bike with the given brand, model name and frame material (no year). */
    private static Bicycle bike(String brand, String name, Material material) {
        return new Bicycle(new Brand(brand, null, null), new BicycleType("Road Bike", "x", null, null, null),
                material, name, null);
    }

    /** Picks the row with this label (e.g. "Brand"), or throws (failing the test) if there is none. */
    private static CompareRow row(List<CompareRow> rows, String label) {
        return rows.stream().filter(r -> r.getLabel().equals(label)).findFirst().orElseThrow();
    }

    @Test
    void knownDifferentValuesAreMarkedAsDifferent() {
        Bicycle a = bike("Canyon", "Speedmax CFR", new Material("Carbon Fibre", "x"));
        Bicycle b = bike("Canyon", "Endurace", new Material("Carbon Fibre", "x"));
        // Bicycle has no setters, so ReflectionTestUtils.setField writes the private field directly
        ReflectionTestUtils.setField(a, "brakeSystem", "Disc");
        ReflectionTestUtils.setField(b, "brakeSystem", "disc ");

        List<CompareRow> rows = CompareRow.forBicycles(a, b);

        // One row per specification, from Brand to Number of gears
        assertThat(rows).hasSize(10);
        assertThat(row(rows, "Model").isDifferent()).isTrue();
        assertThat(row(rows, "Brand").isDifferent()).isFalse();
        assertThat(row(rows, "Frame material").isDifferent()).isFalse();
        // Capitals and extra spaces do not count as a difference
        assertThat(row(rows, "Brake system").isDifferent()).isFalse();
    }

    // A missing frame material shows "Not confirmed", other missing values show "Not specified",
    // and a row with a missing value is never marked as different (it cannot be compared)
    @Test
    void missingValuesAreShownHonestlyAndNeverCountAsDifferent() {
        Bicycle a = bike("Brompton", "Folding Bicycle", null);
        Bicycle b = bike("Cervélo", "S5", new Material("Carbon Fibre", "x"));
        ReflectionTestUtils.setField(b, "year", 2019);
        ReflectionTestUtils.setField(b, "wheelSize", "700c");
        ReflectionTestUtils.setField(a, "wheelSize", "  ");

        List<CompareRow> rows = CompareRow.forBicycles(a, b);

        CompareRow frame = row(rows, "Frame material");
        assertThat(frame.getValueA()).isEqualTo("Not confirmed");
        assertThat(frame.isMissingA()).isTrue();
        assertThat(frame.isMissingInformation()).isTrue();
        assertThat(frame.isDifferent()).isFalse();

        CompareRow year = row(rows, "Year");
        assertThat(year.getValueA()).isEqualTo("Not specified");
        assertThat(year.getValueB()).isEqualTo("2019");
        assertThat(year.isDifferent()).isFalse();

        // A blank value is treated as missing, not as a real value
        assertThat(row(rows, "Wheel size").getValueA()).isEqualTo("Not specified");

        CompareRow gears = row(rows, "Number of gears");
        assertThat(gears.getValueA()).isEqualTo("Not specified");
        assertThat(gears.getValueB()).isEqualTo("Not specified");
        assertThat(gears.isDifferent()).isFalse();
    }

    @Test
    void weightIsShownInKilogramsWithoutTrailingZeros() {
        Bicycle a = bike("Tsunami", "SNM100", null);
        Bicycle b = bike("BXT", "145", null);
        ReflectionTestUtils.setField(a, "weightKg", new BigDecimal("8.40"));
        ReflectionTestUtils.setField(b, "weightKg", new BigDecimal("7.65"));

        CompareRow weight = row(CompareRow.forBicycles(a, b), "Weight");

        assertThat(weight.getValueA()).isEqualTo("8.4 kg");
        assertThat(weight.getValueB()).isEqualTo("7.65 kg");
        assertThat(weight.isDifferent()).isTrue();
    }
}
