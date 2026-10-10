package com.veloverse.service;

// Static imports let the test call these helper methods without the class name:
// assertThat(...) (AssertJ) checks a result, when(...) (Mockito) tells a mock what to return,
// and lenient() relaxes Mockito's "every stub must be used" check (see allBicycles below).
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.veloverse.model.Bicycle;
import com.veloverse.model.BicycleType;
import com.veloverse.model.Brand;
import com.veloverse.model.Material;
import com.veloverse.repository.BicycleRepository;

/**
 * Unit tests for the search and filter rules in BicycleService.
 * The repository is mocked, so no database is needed.
 * A unit test checks one class on its own: Spring is not started at all, only plain Java and Mockito.
 * Each test follows arrange (prepare data and mocks), act (call the service) and assert (check the result),
 * with act and assert often written together on one line, e.g. assertThat(service.search(...)).
 */
// @ExtendWith(MockitoExtension.class) lets Mockito create the @Mock and @InjectMocks fields before each test.
@ExtendWith(MockitoExtension.class)
class BicycleServiceTest {

    // @Mock: a fake BicycleRepository. It never touches a database and returns only what a test tells it to.
    @Mock
    private BicycleRepository repository;

    // @InjectMocks: a real BicycleService, created by Mockito with the mock repository passed to its constructor.
    @InjectMocks
    private BicycleService service;

    /**
     * Gives a test entity an ID and returns it.
     * The entities have no setters (the database normally sets the ID), so ReflectionTestUtils.setField
     * writes the private "id" field directly. Generic <T> means it works for Brand, Bicycle, Material and so on.
     */
    private static <T> T withId(T entity, int id) {
        ReflectionTestUtils.setField(entity, "id", id);
        return entity;
    }

    // Three brands, two types and two materials, mixed across three bicycles.
    // private final: set once and never changed. JUnit creates a new test object for every test,
    // so each test gets fresh copies of this test data.
    private final Brand cervelo = withId(new Brand("Cervélo", "Canada", null), 1);
    private final Brand giant = withId(new Brand("Giant", "Taiwan", null), 2);
    private final Brand tsunami = withId(new Brand("Tsunami", null, null), 3);
    private final BicycleType road = withId(new BicycleType("Road Bike", "x", null, null, null), 1);
    private final BicycleType fixedGear = withId(new BicycleType("Fixed-Gear Bike", "x", null, null, null), 6);
    private final Material aluminium = withId(new Material("Aluminium", "x"), 1);
    private final Material carbon = withId(new Material("Carbon Fibre", "x"), 2);

    private final Bicycle s5 = withId(new Bicycle(cervelo, road, carbon, "S5", 2026), 1);
    private final Bicycle tcr = withId(new Bicycle(giant, road, carbon, "TCR Advanced Pro 2", 2025), 2);
    private final Bicycle snm100 = withId(new Bicycle(tsunami, fixedGear, aluminium, "SNM100 Complete Fixie", 2021), 3);

    // @BeforeEach runs before every @Test method, so each test starts with the mock returning all three bicycles.
    @BeforeEach
    void allBicycles() {
        // lenient: one test below replaces this list with its own.
        // MockitoExtension normally fails a test whose stub is never used (UnnecessaryStubbingException);
        // lenient() switches that check off for this one stub, so the replacing test does not fail.
        lenient().when(repository.findAllByOrderByIdAsc()).thenReturn(List.of(s5, tcr, snm100));
    }

    // ---------- Search ----------

    // containsExactly checks both which bicycles are returned and their order
    @Test
    void emptyOrBlankSearchReturnsEveryBicycle() {
        assertThat(service.search(null)).containsExactly(s5, tcr, snm100);
        assertThat(service.search("")).containsExactly(s5, tcr, snm100);
        assertThat(service.search("   ")).containsExactly(s5, tcr, snm100);
    }

    @Test
    void searchMatchesModelBrandOrTypeIgnoringCase() {
        // Model name, including part of a name
        assertThat(service.search("tcr")).containsExactly(tcr);
        assertThat(service.search("SNM")).containsExactly(snm100);
        // Brand name, with surrounding spaces trimmed
        assertThat(service.search("  GIANT  ")).containsExactly(tcr);
        // Bicycle type
        assertThat(service.search("road bike")).containsExactly(s5, tcr);
        assertThat(service.search("fixed-gear")).containsExactly(snm100);
    }

    @Test
    void searchIgnoresAccents() {
        assertThat(service.search("cervelo")).containsExactly(s5);
        assertThat(service.search("Cervélo")).containsExactly(s5);
    }

    @Test
    void searchWithNoMatchReturnsNothing() {
        assertThat(service.search("unicycle")).isEmpty();
        // % and _ are normal characters, not "match anything"
        assertThat(service.search("%")).isEmpty();
        assertThat(service.search("_")).isEmpty();
    }

    // ---------- Filters ----------

    // search(query, typeId, brandId, materialId): a null ID means "All" for that filter
    @Test
    void noFiltersReturnsEveryBicycle() {
        assertThat(service.search(null, null, null, null)).containsExactly(s5, tcr, snm100);
    }

    // Type 6 = fixed-gear, brand 2 = Giant, material 2 = carbon (IDs given by withId above)
    @Test
    void eachFilterOnItsOwnKeepsOnlyMatchingBicycles() {
        assertThat(service.search(null, 6, null, null)).containsExactly(snm100);
        assertThat(service.search(null, null, 2, null)).containsExactly(tcr);
        assertThat(service.search(null, null, null, 2)).containsExactly(s5, tcr);
    }

    // Road (1) + Cervélo (1) + carbon (2) matches only the S5
    @Test
    void filtersCanBeCombinedAndAllMustMatch() {
        assertThat(service.search(null, 1, 1, 2)).containsExactly(s5);
        // Tsunami makes no carbon fixed-gear bike in this data
        assertThat(service.search(null, 6, 3, 2)).isEmpty();
    }

    // "road" finds the S5 and TCR first, then the brand filter keeps only Giant (2); Tsunami (3) has no road bike
    @Test
    void filtersAreAppliedToTheSearchResults() {
        assertThat(service.search("road", null, 2, null)).containsExactly(tcr);
        assertThat(service.search("road", null, 3, null)).isEmpty();
    }

    @Test
    void materialFilterSkipsBicyclesWithoutAConfirmedFrameMaterial() {
        // Arrange: a bicycle whose frame material is null, and a mock list that replaces the one from allBicycles()
        Brand brompton = withId(new Brand("Brompton", "United Kingdom", null), 4);
        BicycleType folding = withId(new BicycleType("Folding Bike", "x", null, null, null), 9);
        Bicycle foldingBike = withId(new Bicycle(brompton, folding, null, "Folding Bicycle", null), 4);
        when(repository.findAllByOrderByIdAsc()).thenReturn(List.of(s5, foldingBike));

        // Filtering by carbon must not fail on the bicycle with no material, and must leave it out
        assertThat(service.search(null, null, null, 2)).containsExactly(s5);
        // Without a material filter the bicycle is still listed and searchable
        assertThat(service.search(null, null, null, null)).containsExactly(s5, foldingBike);
        assertThat(service.search("folding", 9, 4, null)).containsExactly(foldingBike);
    }
}
