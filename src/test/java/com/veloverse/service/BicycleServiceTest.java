package com.veloverse.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

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
 * Unit tests for the search rules in BicycleService. The repository is mocked, so no database is needed.
 */
@ExtendWith(MockitoExtension.class)
class BicycleServiceTest {

    @Mock
    private BicycleRepository repository;

    @InjectMocks
    private BicycleService service;

    @Test
    void emptyOrBlankSearchReturnsEveryBicycle() {
        List<Bicycle> all = List.of();
        when(repository.findAllByOrderByIdAsc()).thenReturn(all);

        assertThat(service.search(null)).isSameAs(all);
        assertThat(service.search("")).isSameAs(all);
        assertThat(service.search("   ")).isSameAs(all);
        verify(repository, never())
                .findByNameContainingIgnoreCaseOrBrandNameContainingIgnoreCaseOrTypeNameContainingIgnoreCaseOrderByIdAsc(
                        anyString(), anyString(), anyString());
    }

    @Test
    void searchTextIsTrimmedAndCheckedAgainstModelBrandAndType() {
        service.search("  trek  ");

        verify(repository)
                .findByNameContainingIgnoreCaseOrBrandNameContainingIgnoreCaseOrTypeNameContainingIgnoreCaseOrderByIdAsc(
                        "trek", "trek", "trek");
    }

    // ---------- Filters ----------

    private static <T> T withId(T entity, int id) {
        ReflectionTestUtils.setField(entity, "id", id);
        return entity;
    }

    // Two brands, two types and two materials, mixed across three bicycles
    private final Brand trek = withId(new Brand("Trek", null, null), 1);
    private final Brand giant = withId(new Brand("Giant", null, null), 3);
    private final BicycleType road = withId(new BicycleType("Road Bike", "x", null, null, null), 2);
    private final BicycleType touring = withId(new BicycleType("Touring Bike", "x", null, null, null), 6);
    private final Material steel = withId(new Material("Steel", "x"), 1);
    private final Material carbon = withId(new Material("Carbon Fibre", "x"), 3);

    private final Bicycle disc520 = withId(new Bicycle(trek, touring, steel, "520 Disc", 2023), 1);
    private final Bicycle domane = withId(new Bicycle(trek, road, carbon, "Domane SL 5 Gen 4", 2024), 6);
    private final Bicycle escape = withId(new Bicycle(giant, touring, steel, "Escape 3", 2022), 3);

    @Test
    void noFiltersReturnsEveryBicycle() {
        when(repository.findAllByOrderByIdAsc()).thenReturn(List.of(disc520, escape, domane));

        assertThat(service.search(null, null, null, null)).containsExactly(disc520, escape, domane);
    }

    @Test
    void eachFilterOnItsOwnKeepsOnlyMatchingBicycles() {
        when(repository.findAllByOrderByIdAsc()).thenReturn(List.of(disc520, escape, domane));

        assertThat(service.search(null, 2, null, null)).containsExactly(domane);
        assertThat(service.search(null, null, 1, null)).containsExactly(disc520, domane);
        assertThat(service.search(null, null, null, 1)).containsExactly(disc520, escape);
    }

    @Test
    void filtersCanBeCombinedAndAllMustMatch() {
        when(repository.findAllByOrderByIdAsc()).thenReturn(List.of(disc520, escape, domane));

        assertThat(service.search(null, 6, 1, 1)).containsExactly(disc520);
        assertThat(service.search(null, 6, 3, 1)).containsExactly(escape);
        // Trek makes no carbon touring bike in this data
        assertThat(service.search(null, 6, 1, 3)).isEmpty();
    }

    @Test
    void filtersAreAppliedToTheSearchResults() {
        when(repository
                .findByNameContainingIgnoreCaseOrBrandNameContainingIgnoreCaseOrTypeNameContainingIgnoreCaseOrderByIdAsc(
                        "trek", "trek", "trek"))
                .thenReturn(List.of(disc520, domane));

        assertThat(service.search("trek", 2, null, null)).containsExactly(domane);
        assertThat(service.search("trek", null, null, 1)).containsExactly(disc520);
    }
}
