package com.veloverse.controller;

// Static imports let the tests call these methods without their class name:
// get builds a fake GET request, status / view / content check the response,
// containsString (Hamcrest) checks the page text, and when (Mockito) sets what a mock returns.
// not(...) turns a check around, e.g. the page must NOT contain some text.
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
 * Web-layer tests for the Bicycles list (including search and filters) and detail pages, with the service mocked.
 * @WebMvcTest starts only the web layer (this controller plus Thymeleaf), not the services or the database.
 * Each test follows arrange (tell the mock service what to return), act (send a fake request with
 * MockMvc) and assert (check the status, the template name and the rendered HTML).
 */
@WebMvcTest(BicycleController.class)
class BicycleControllerTest {

    // MockMvc sends fake HTTP requests to the controller without starting a real web server.
    // @Autowired asks Spring to put its MockMvc object into this field.
    @Autowired
    private MockMvc mockMvc;

    // @MockitoBean: Spring gives the controller a Mockito mock in place of the real service.
    // A mock returns only what a test sets with when(...), otherwise an empty list, an empty Optional or null.
    @MockitoBean
    private BicycleService bicycleService;

    // A bicycle with only the required fields and a description, so other specifications are missing.
    // The entities have no setters (the database normally sets these values), so ReflectionTestUtils.setField
    // writes the private fields such as "id" and "description" directly.
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

    // With no parameters the controller calls search(null, null, null, null), so that exact call is stubbed
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

    // Security check: the search text is shown back on the page, so it must appear as &lt;b&gt; text, not as a real <b> tag
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

    // The service finds nothing (empty Optional), so the controller answers 404 Not Found
    @Test
    void unknownBicycleIdReturnsNotFound() throws Exception {
        when(bicycleService.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/bicycles/999"))
                .andExpect(status().isNotFound());
    }

    // The URL pattern only accepts digits, so "abc" matches no controller method and gives 404 instead of a crash
    @Test
    void nonNumericBicycleIdReturnsNotFound() throws Exception {
        mockMvc.perform(get("/bicycles/abc"))
                .andExpect(status().isNotFound());
    }

    // ---------- Filters ----------

    /** Arrange helper: makes the mock return the options for the Type, Brand and Material dropdowns. */
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

    // The text parameters "9", "5" and "1" must reach the service as the numbers 9, 5 and 1;
    // the stub only answers that exact call, so a wrong value would leave the list empty
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

    // "abc", "-1" and "" are not valid IDs, so the controller treats each one as "All" (null)
    @Test
    void invalidFilterValuesAreIgnoredInsteadOfCausingAnError() throws Exception {
        when(bicycleService.search(null, null, null, null)).thenReturn(List.of(brompton()));

        mockMvc.perform(get("/bicycles").param("type", "abc").param("brand", "-1").param("material", ""))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("C Line Explore")));
    }

    // Features, advantages and disadvantages are stored as text with one item per line ("\n") and shown as list items.
    // "&amp;" is how the HTML writes the "&" in "Pros & Cons".
    @Test
    void detailPageShowsIntendedUseFeaturesProsConsAndSource() throws Exception {
        Brand tsunami = new Brand("Tsunami", null, null);
        ReflectionTestUtils.setField(tsunami, "id", 3);
        BicycleType fixedGear = new BicycleType("Fixed-Gear Bike", "One fixed gear.", null, null, null);
        ReflectionTestUtils.setField(fixedGear, "id", 6);
        Material aluminium = new Material("Aluminium", "Light and affordable.");
        ReflectionTestUtils.setField(aluminium, "id", 1);
        Bicycle snm100 = new Bicycle(tsunami, fixedGear, aluminium, "SNM100 Complete Fixie", 2021);
        ReflectionTestUtils.setField(snm100, "id", 3);
        ReflectionTestUtils.setField(snm100, "intendedUse", "Riding around town and on flat roads.");
        ReflectionTestUtils.setField(snm100, "features", "Aluminium frame\nFront rim brake included");
        ReflectionTestUtils.setField(snm100, "advantages", "Very simple\nLight for the price");
        ReflectionTestUtils.setField(snm100, "disadvantages", "Only one gear");
        ReflectionTestUtils.setField(snm100, "sourceUrl", "https://tsunamibicycle.com/product/snm100/");
        when(bicycleService.findById(3)).thenReturn(Optional.of(snm100));

        mockMvc.perform(get("/bicycles/3"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("What is it designed for?")))
                .andExpect(content().string(containsString("Riding around town and on flat roads.")))
                .andExpect(content().string(containsString("<li>Front rim brake included</li>")))
                .andExpect(content().string(containsString("Pros &amp; Cons")))
                .andExpect(content().string(containsString("<li>Light for the price</li>")))
                .andExpect(content().string(containsString("<li>Only one gear</li>")))
                .andExpect(content().string(containsString("href=\"https://tsunamibicycle.com/product/snm100/\"")));
    }

    @Test
    void detailPageHidesEducationalSectionsWhenThereIsNoContent() throws Exception {
        // domane() has specifications but no intended use, features, pros, cons or source
        when(bicycleService.findById(6)).thenReturn(Optional.of(domane()));

        mockMvc.perform(get("/bicycles/6"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("What is it designed for?"))))
                .andExpect(content().string(not(containsString("Pros &amp; Cons"))))
                .andExpect(content().string(not(containsString("Specifications checked against"))));
    }

    // A real model from the collection whose frame material is not confirmed (stored as NULL)
    private static Bicycle bromptonWithoutFrameMaterial() {
        Brand brand = new Brand("Brompton", "United Kingdom", "A folding bike maker.");
        ReflectionTestUtils.setField(brand, "id", 27);
        BicycleType type = new BicycleType("Folding Bike", "Folds down small.", null, null, null);
        ReflectionTestUtils.setField(type, "id", 9);
        Bicycle bike = new Bicycle(brand, type, null, "Folding Bicycle", null);
        ReflectionTestUtils.setField(bike, "id", 18);
        return bike;
    }

    // The Canyon Speedmax CFR, which has an approved CC BY-SA photo
    private static Bicycle speedmax() {
        Brand canyon = new Brand("Canyon", "Germany", "A German bicycle company.");
        ReflectionTestUtils.setField(canyon, "id", 30);
        BicycleType tt = new BicycleType("Time-Trial / Triathlon Bike", "Built for riding against the clock.", null, null, null);
        ReflectionTestUtils.setField(tt, "id", 10);
        Material carbon = new Material("Carbon Fibre", "Light and stiff.");
        ReflectionTestUtils.setField(carbon, "id", 2);
        Bicycle bike = new Bicycle(canyon, tt, carbon, "Speedmax CFR", 2020);
        ReflectionTestUtils.setField(bike, "id", 21);
        return bike;
    }

    @Test
    void unknownFrameMaterialIsShownAsNotConfirmed() throws Exception {
        when(bicycleService.search(null, null, null, null)).thenReturn(List.of(bromptonWithoutFrameMaterial()));
        when(bicycleService.findById(18)).thenReturn(Optional.of(bromptonWithoutFrameMaterial()));

        mockMvc.perform(get("/bicycles"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Not confirmed")));

        mockMvc.perform(get("/bicycles/18"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<dd>Not confirmed</dd>")))
                // No link to a material page when there is no material
                .andExpect(content().string(not(containsString("href=\"/materials/"))));
    }

    // A CC BY-SA licence requires the author, a source link, a licence link and a note that the photo was changed
    @Test
    void ccBySaPhotoShowsAuthorSourceLicenceAndChanges() throws Exception {
        when(bicycleService.findById(21)).thenReturn(Optional.of(speedmax()));

        mockMvc.perform(get("/bicycles/21"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/images/photos/bike-canyon-speedmax-cfr-1280.webp")))
                .andExpect(content().string(containsString("Peter Ehrit Werbefotografie GmbH / Canyon Bicycles GmbH")))
                .andExpect(content().string(containsString("href=\"https://commons.wikimedia.org/wiki/File:Speedmax-cfr-blue-2020.jpg\"")))
                .andExpect(content().string(containsString("href=\"https://creativecommons.org/licenses/by-sa/4.0/\"")))
                .andExpect(content().string(containsString("This adapted version is licensed under CC BY-SA 4.0.")))
                .andExpect(content().string(not(containsString("Placeholder image"))));
    }

    // The list card for the Cervélo S5 shows its CC BY photo credit, and the page has a trademark notice
    @Test
    void ccByPhotoShowsLicenceAndChangesOnTheList() throws Exception {
        Brand cervelo = new Brand("Cervélo", "Canada", null);
        ReflectionTestUtils.setField(cervelo, "id", 26);
        BicycleType road = new BicycleType("Road Bike", "Fast on paved roads.", null, null, null);
        Material carbon = new Material("Carbon Fibre", "Light and stiff.");
        Bicycle s5 = new Bicycle(cervelo, road, carbon, "S5", 2019);
        ReflectionTestUtils.setField(s5, "id", 17);
        when(bicycleService.search(null, null, null, null)).thenReturn(List.of(s5));

        mockMvc.perform(get("/bicycles"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/images/photos/bike-cervelo-s5-640.webp")))
                .andExpect(content().string(containsString("Glory Cycles")))
                .andExpect(content().string(containsString("href=\"https://creativecommons.org/licenses/by/2.0/\"")))
                .andExpect(content().string(containsString("Cropped and resized by VeloVerse.")))
                .andExpect(content().string(containsString("trademarks of their owners")));
    }

    @Test
    void modelWithoutAnApprovedPhotoKeepsThePlaceholder() throws Exception {
        // brompton() is a test model ("C Line Explore") that has no photo
        when(bicycleService.findById(5)).thenReturn(Optional.of(brompton()));

        mockMvc.perform(get("/bicycles/5"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("placeholder-bike.svg")))
                .andExpect(content().string(not(containsString("photo-licensed"))));
    }
}
