package com.veloverse.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.veloverse.model.Bicycle;
import com.veloverse.model.BicycleType;
import com.veloverse.model.Brand;
import com.veloverse.model.Material;
import com.veloverse.service.BicycleService;

/**
 * Web-layer tests for the Bicycles list (including search and filters) and detail pages, with the services mocked.
 */
@WebMvcTest(BicycleController.class)
class BicycleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BicycleService bicycleService;

    // A bicycle with only the required fields and a description, so other specifications are missing
    private static Bicycle brompton() {
        Brand brand = new Brand("Brompton", "United Kingdom", "A folding bike maker.");
        ReflectionTestUtils.setField(brand, "id", 5);
        BicycleType type = new BicycleType("Folding Bike", "Folds down small.", null, null, null);
        ReflectionTestUtils.setField(type, "id", 9);
        Material steel = new Material("Steel", "Strong and comfortable.");
        ReflectionTestUtils.setField(steel, "id", 1);

        Bicycle bike = new Bicycle(brand, type, steel, "C Line Explore", 2024);
        ReflectionTestUtils.setField(bike, "id", 5);
        ReflectionTestUtils.setField(bike, "description", "A compact folding bike for city trips.");
        return bike;
    }

    // A bicycle with every specification filled in
    private static Bicycle domane() {
        Brand brand = new Brand("Trek", "United States", "An American bicycle company.");
        ReflectionTestUtils.setField(brand, "id", 1);
        BicycleType type = new BicycleType("Road Bike", "Built for speed on paved roads.", null, null, null);
        ReflectionTestUtils.setField(type, "id", 2);
        Material carbon = new Material("Carbon Fibre", "Light and stiff.");
        ReflectionTestUtils.setField(carbon, "id", 3);

        Bicycle bike = new Bicycle(brand, type, carbon, "Domane SL 5 Gen 4", 2024);
        ReflectionTestUtils.setField(bike, "id", 6);
        ReflectionTestUtils.setField(bike, "weightKg", new BigDecimal("8.93"));
        ReflectionTestUtils.setField(bike, "wheelSize", "700c");
        ReflectionTestUtils.setField(bike, "brakeSystem", "Hydraulic disc");
        ReflectionTestUtils.setField(bike, "drivetrain", "Shimano 105 2 x 12-speed");
        ReflectionTestUtils.setField(bike, "gears", 24);
        ReflectionTestUtils.setField(bike, "description", "A carbon endurance road bike.");
        return bike;
    }

    @Test
    void listPageShowsBrandModelYearTypeMaterialDescriptionAndLearnMoreLink() throws Exception {
        when(bicycleService.search(null, null, null, null)).thenReturn(List.of(brompton()));

        mockMvc.perform(get("/bicycles"))
                .andExpect(status().isOk())
                .andExpect(view().name("bicycles"))
                .andExpect(content().string(containsString("Brompton")))
                .andExpect(content().string(containsString("C Line Explore")))
                .andExpect(content().string(containsString("2024")))
                .andExpect(content().string(containsString("Folding Bike")))
                .andExpect(content().string(containsString("Steel")))
                .andExpect(content().string(containsString("A compact folding bike for city trips.")))
                .andExpect(content().string(containsString("href=\"/bicycles/5\"")))
                .andExpect(content().string(not(containsString("Bicycle details coming soon"))));
    }

    @Test
    void listPageShowsEmptyStateWhenThereAreNoBicycles() throws Exception {
        when(bicycleService.search(null, null, null, null)).thenReturn(List.of());

        mockMvc.perform(get("/bicycles"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("No bicycles have been added yet.")))
                // The search box is still shown, empty
                .andExpect(content().string(containsString("name=\"q\"")));
    }

    @Test
    void searchShowsMatchingBicyclesAndKeepsTheSearchTextInTheBox() throws Exception {
        when(bicycleService.search("folding", null, null, null)).thenReturn(List.of(brompton()));

        mockMvc.perform(get("/bicycles").param("q", "folding"))
                .andExpect(status().isOk())
                .andExpect(view().name("bicycles"))
                .andExpect(content().string(containsString("C Line Explore")))
                .andExpect(content().string(containsString("href=\"/bicycles/5\"")))
                .andExpect(content().string(containsString("value=\"folding\"")))
                .andExpect(content().string(containsString("1 bicycle found for “folding”")))
                .andExpect(content().string(containsString("Clear search")));
    }

    @Test
    void searchWithNoMatchesShowsNoBicyclesFound() throws Exception {
        when(bicycleService.search("unicycle", null, null, null)).thenReturn(List.of());

        mockMvc.perform(get("/bicycles").param("q", "unicycle"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("No bicycles found.")))
                .andExpect(content().string(not(containsString("No bicycles have been added yet."))));
    }

    @Test
    void searchTextIsEscapedSoItCannotInjectHtml() throws Exception {
        when(bicycleService.search("<b>x</b>", null, null, null)).thenReturn(List.of());

        mockMvc.perform(get("/bicycles").param("q", "<b>x</b>"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("<b>x</b>"))))
                .andExpect(content().string(containsString("&lt;b&gt;x&lt;/b&gt;")));
    }

    @Test
    void detailPageShowsAllSpecificationsBreadcrumbAndBackLink() throws Exception {
        when(bicycleService.findById(6)).thenReturn(Optional.of(domane()));

        mockMvc.perform(get("/bicycles/6"))
                .andExpect(status().isOk())
                .andExpect(view().name("bicycle-detail"))
                .andExpect(content().string(containsString("Trek")))
                .andExpect(content().string(containsString("Domane SL 5 Gen 4")))
                .andExpect(content().string(containsString("2024")))
                .andExpect(content().string(containsString("Road Bike")))
                .andExpect(content().string(containsString("Carbon Fibre")))
                .andExpect(content().string(containsString("8.93 kg")))
                .andExpect(content().string(containsString("700c")))
                .andExpect(content().string(containsString("Hydraulic disc")))
                .andExpect(content().string(containsString("Shimano 105 2 x 12-speed")))
                .andExpect(content().string(containsString("24")))
                .andExpect(content().string(containsString("A carbon endurance road bike.")))
                .andExpect(content().string(containsString("aria-label=\"Breadcrumb\"")))
                .andExpect(content().string(containsString("Back to all bicycles")))
                .andExpect(content().string(containsString("href=\"/bicycles\"")))
                .andExpect(content().string(not(containsString("Not specified"))));
    }

    @Test
    void detailPageShowsNotSpecifiedForMissingSpecifications() throws Exception {
        // brompton() has no weight, wheel size, brakes, drivetrain or gears set
        when(bicycleService.findById(5)).thenReturn(Optional.of(brompton()));

        mockMvc.perform(get("/bicycles/5"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("C Line Explore")))
                .andExpect(content().string(containsString("Not specified")));
    }

    @Test
    void unknownBicycleIdReturnsNotFound() throws Exception {
        when(bicycleService.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/bicycles/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void nonNumericBicycleIdReturnsNotFound() throws Exception {
        mockMvc.perform(get("/bicycles/abc"))
                .andExpect(status().isNotFound());
    }

    // ---------- Filters ----------

    private void stubDropdownOptions() {
        BicycleType folding = new BicycleType("Folding Bike", "Folds down small.", null, null, null);
        ReflectionTestUtils.setField(folding, "id", 9);
        BicycleType road = new BicycleType("Road Bike", "Fast on paved roads.", null, null, null);
        ReflectionTestUtils.setField(road, "id", 2);
        Brand brompton = new Brand("Brompton", "United Kingdom", null);
        ReflectionTestUtils.setField(brompton, "id", 5);
        Material steel = new Material("Steel", "Strong and comfortable.");
        ReflectionTestUtils.setField(steel, "id", 1);

        when(bicycleService.findTypesInUse()).thenReturn(List.of(folding, road));
        when(bicycleService.findBrandsInUse()).thenReturn(List.of(brompton));
        when(bicycleService.findFrameMaterialsInUse()).thenReturn(List.of(steel));
    }

    @Test
    void listPageShowsThreeFilterDropdownsWithAllOptionsAndDatabaseValues() throws Exception {
        stubDropdownOptions();
        when(bicycleService.search(null, null, null, null)).thenReturn(List.of(brompton()));

        mockMvc.perform(get("/bicycles"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("name=\"type\"")))
                .andExpect(content().string(containsString("name=\"brand\"")))
                .andExpect(content().string(containsString("name=\"material\"")))
                .andExpect(content().string(containsString("<option value=\"\">All types</option>")))
                .andExpect(content().string(containsString("<option value=\"\">All brands</option>")))
                .andExpect(content().string(containsString("<option value=\"\">All materials</option>")))
                .andExpect(content().string(containsString("<option value=\"9\">Folding Bike</option>")))
                .andExpect(content().string(containsString("<option value=\"5\">Brompton</option>")))
                .andExpect(content().string(containsString("<option value=\"1\">Steel</option>")))
                // Nothing is filtered yet, so there is no Clear filters link
                .andExpect(content().string(not(containsString("Clear filters"))));
    }

    @Test
    void filtersArePassedToTheServiceAndStaySelected() throws Exception {
        stubDropdownOptions();
        when(bicycleService.search(null, 9, 5, 1)).thenReturn(List.of(brompton()));

        mockMvc.perform(get("/bicycles").param("type", "9").param("brand", "5").param("material", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("C Line Explore")))
                .andExpect(content().string(containsString("href=\"/bicycles/5\"")))
                .andExpect(content().string(containsString("<option value=\"9\" selected=\"selected\">Folding Bike</option>")))
                .andExpect(content().string(containsString("<option value=\"5\" selected=\"selected\">Brompton</option>")))
                .andExpect(content().string(containsString("<option value=\"1\" selected=\"selected\">Steel</option>")))
                .andExpect(content().string(containsString("1 bicycle found with the selected filters")))
                .andExpect(content().string(containsString("Clear filters")));
    }

    @Test
    void filtersWorkTogetherWithTheSearchText() throws Exception {
        stubDropdownOptions();
        when(bicycleService.search("brompton", 9, null, null)).thenReturn(List.of(brompton()));

        mockMvc.perform(get("/bicycles").param("q", "brompton").param("type", "9").param("brand", ""))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("C Line Explore")))
                .andExpect(content().string(containsString("value=\"brompton\"")))
                .andExpect(content().string(containsString("1 bicycle found for “brompton” with the selected filters")))
                // Clear filters keeps the search text
                .andExpect(content().string(containsString("href=\"/bicycles?q=brompton\"")));
    }

    @Test
    void filtersWithNoMatchesShowAHelpfulMessage() throws Exception {
        stubDropdownOptions();
        when(bicycleService.search(null, 2, 5, null)).thenReturn(List.of());

        mockMvc.perform(get("/bicycles").param("type", "2").param("brand", "5"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("No bicycles found.")))
                .andExpect(content().string(containsString("No bicycle matches all of the selected filters.")))
                .andExpect(content().string(not(containsString("No bicycles have been added yet."))));
    }

    @Test
    void invalidFilterValuesAreIgnoredInsteadOfCausingAnError() throws Exception {
        when(bicycleService.search(null, null, null, null)).thenReturn(List.of(brompton()));

        mockMvc.perform(get("/bicycles").param("type", "abc").param("brand", "-1").param("material", ""))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("C Line Explore")));
    }
}
