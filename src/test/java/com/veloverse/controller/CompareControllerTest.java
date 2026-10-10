package com.veloverse.controller;

// Static imports let the tests call these methods without their class name:
// get builds a fake GET request, status / view / content check the response,
// containsString (Hamcrest) checks the page text, and when (Mockito) sets what a mock returns.
// not(...) turns a check around, and assertThat (AssertJ) checks plain Java values such as the HTML string.
import static org.assertj.core.api.Assertions.assertThat;
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

import org.junit.jupiter.api.BeforeEach;
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
 * Web-layer tests for the Compare Bicycles page, with the service mocked.
 * @WebMvcTest starts only the web layer (this controller plus Thymeleaf), not the services or the database.
 * Each test follows arrange (tell the mock service what to return), act (send a fake request with
 * MockMvc) and assert (check the status, the template name and the rendered HTML).
 */
@WebMvcTest(CompareController.class)
class CompareControllerTest {

    // MockMvc sends fake HTTP requests to the controller without starting a real web server.
    // @Autowired asks Spring to put its MockMvc object into this field.
    @Autowired
    private MockMvc mockMvc;

    // @MockitoBean: Spring gives the controller a Mockito mock in place of the real service.
    // A mock returns only what a test sets with when(...), otherwise an empty list, an empty Optional or null.
    @MockitoBean
    private BicycleService bicycleService;

    // The two test bicycles, filled in by setUp() before every test
    private Bicycle touring;
    private Bicycle road;

    /**
     * Gives a test entity an ID and returns it.
     * The entities have no setters (the database normally sets the ID), so ReflectionTestUtils.setField
     * writes the private field directly; setUp() uses it the same way for the other specifications.
     */
    private static <T> T withId(T entity, int id) {
        ReflectionTestUtils.setField(entity, "id", id);
        return entity;
    }

    // @BeforeEach runs before every @Test: it builds the two Trek bicycles and tells the mock service about them.
    // Not every test uses every stub; that is fine here because @MockitoBean mocks do not report unused stubs.
    @BeforeEach
    void setUp() {
        Brand trek = withId(new Brand("Trek", "United States", null), 1);
        Material steel = withId(new Material("Steel", "x"), 3);
        Material carbon = withId(new Material("Carbon Fibre", "x"), 2);

        // A touring bike with no weight stored, so the page must show "Not specified"
        touring = withId(new Bicycle(trek, withId(new BicycleType("Touring Bike", "x", null, null, null), 7),
                steel, "520 Disc", 2023), 1);
        ReflectionTestUtils.setField(touring, "wheelSize", "700c");
        ReflectionTestUtils.setField(touring, "brakeSystem", "Mechanical disc");
        ReflectionTestUtils.setField(touring, "drivetrain", "3 x 9-speed Shimano");
        ReflectionTestUtils.setField(touring, "gears", 27);

        road = withId(new Bicycle(trek, withId(new BicycleType("Road Bike", "x", null, null, null), 1),
                carbon, "Domane SL 5 Gen 4", 2024), 6);
        ReflectionTestUtils.setField(road, "weightKg", new BigDecimal("8.93"));
        ReflectionTestUtils.setField(road, "wheelSize", "700c");
        ReflectionTestUtils.setField(road, "brakeSystem", "Hydraulic disc");
        ReflectionTestUtils.setField(road, "drivetrain", "2 x 12-speed Shimano 105");
        ReflectionTestUtils.setField(road, "gears", 24);

        when(bicycleService.findAll()).thenReturn(List.of(touring, road));
        when(bicycleService.findById(1)).thenReturn(Optional.of(touring));
        when(bicycleService.findById(6)).thenReturn(Optional.of(road));
        when(bicycleService.findById(999)).thenReturn(Optional.empty());
    }

    // With no a and b parameters, both dropdowns are listed but there is no warning and no comparison table yet
    @Test
    void firstVisitShowsTwoDropdownsAndNoValidationMessage() throws Exception {
        mockMvc.perform(get("/compare"))
                .andExpect(status().isOk())
                .andExpect(view().name("compare"))
                .andExpect(content().string(containsString("Compare Bicycles")))
                .andExpect(content().string(containsString("name=\"a\"")))
                .andExpect(content().string(containsString("name=\"b\"")))
                .andExpect(content().string(containsString("<option value=\"1\">Trek 520 Disc (2023)</option>")))
                .andExpect(content().string(containsString("<option value=\"6\">Trek Domane SL 5 Gen 4 (2024)</option>")))
                .andExpect(content().string(containsString("Choose two different bicycles above")))
                .andExpect(content().string(not(containsString("role=\"alert\""))))
                .andExpect(content().string(not(containsString("Specification</th>"))));
    }

    /**
     * Returns the HTML inside the dropdown with the given name, with all runs of whitespace
     * (including line breaks) collapsed to one space. Thymeleaf keeps the template's line breaks
     * between attributes, so this lets the test check the options without depending on formatting.
     */
    private static String dropdown(String html, String name) {
        int start = html.indexOf("name=\"" + name + "\"");
        int end = html.indexOf("</select>", start);
        assertThat(start).as("dropdown '%s' should exist", name).isGreaterThanOrEqualTo(0);
        return html.substring(start, end).replaceAll("\\s+", " ");
    }

    @Test
    void twoDifferentBicyclesAreShownSideBySide() throws Exception {
        // param("a", "1") adds ?a=1 to the request URL.
        // andReturn().getResponse().getContentAsString() gives back the whole HTML so it can be checked further below.
        String html = mockMvc.perform(get("/compare").param("a", "1").param("b", "6"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Specification</th>")))
                .andExpect(content().string(containsString("520 Disc")))
                .andExpect(content().string(containsString("Domane SL 5 Gen 4")))
                .andExpect(content().string(containsString("Touring Bike")))
                .andExpect(content().string(containsString("Road Bike")))
                .andExpect(content().string(containsString("Steel")))
                .andExpect(content().string(containsString("Carbon Fibre")))
                .andExpect(content().string(containsString("8.93 kg")))
                .andExpect(content().string(containsString("Mechanical disc")))
                .andExpect(content().string(containsString("Hydraulic disc")))
                .andExpect(content().string(containsString("3 x 9-speed Shimano")))
                .andExpect(content().string(containsString("2 x 12-speed Shimano 105")))
                .andExpect(content().string(containsString("<td>27</td>")))
                .andExpect(content().string(containsString("<td>24</td>")))
                // The touring bike has no stored weight
                .andExpect(content().string(containsString("<td class=\"value-missing\">Not specified</td>")))
                // Links to both existing detail pages
                .andExpect(content().string(containsString("href=\"/bicycles/1\"")))
                .andExpect(content().string(containsString("href=\"/bicycles/6\"")))
                .andExpect(content().string(not(containsString("role=\"alert\""))))
                .andReturn().getResponse().getContentAsString();

        // The choices stay selected: bicycle 1 in the first dropdown, bicycle 6 in the second
        assertThat(dropdown(html, "a"))
                .contains("<option value=\"1\" selected=\"selected\">Trek 520 Disc (2023)</option>")
                .contains("<option value=\"6\">Trek Domane SL 5 Gen 4 (2024)</option>");
        assertThat(dropdown(html, "b"))
                .contains("<option value=\"6\" selected=\"selected\">Trek Domane SL 5 Gen 4 (2024)</option>")
                .contains("<option value=\"1\">Trek 520 Disc (2023)</option>");
    }

    // The expected messages are the controller's own constants, so the test does not repeat their text
    @Test
    void choosingOnlyOneBicycleAsksForTwo() throws Exception {
        mockMvc.perform(get("/compare").param("a", "1").param("b", ""))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(CompareController.MESSAGE_CHOOSE_TWO)))
                .andExpect(content().string(not(containsString("Specification</th>"))));
    }

    @Test
    void choosingNoBicyclesAsksForTwo() throws Exception {
        mockMvc.perform(get("/compare").param("a", "").param("b", ""))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(CompareController.MESSAGE_CHOOSE_TWO)));
    }

    @Test
    void choosingTheSameBicycleTwiceAsksForTwoDifferentOnes() throws Exception {
        mockMvc.perform(get("/compare").param("a", "1").param("b", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(CompareController.MESSAGE_SAME_BICYCLE)))
                .andExpect(content().string(not(containsString("Specification</th>"))));
    }

    // Unlike the detail pages, the Compare page does not answer 404: it stays at 200 and shows a message
    @Test
    void unknownBicycleIdShowsNotFoundMessage() throws Exception {
        mockMvc.perform(get("/compare").param("a", "1").param("b", "999"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("could not be found")))
                .andExpect(content().string(not(containsString("Specification</th>"))));
    }

    @Test
    void nonNumericBicycleIdShowsNotFoundMessage() throws Exception {
        mockMvc.perform(get("/compare").param("a", "abc").param("b", "6"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("could not be found")));
    }

    @Test
    void compareLinkIsEnabledInHeaderAndFooter() throws Exception {
        mockMvc.perform(get("/compare"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("href=\"/compare\"")))
                .andExpect(content().string(containsString("aria-current=\"page\">Compare</a>")))
                .andExpect(content().string(not(containsString("Compare <small>Coming soon</small>"))));
    }

    @Test
    void unknownFrameMaterialIsShownAsNotConfirmed() throws Exception {
        Brand orbea = withId(new Brand("Orbea", "Spain", null), 29);
        Bicycle avant = withId(new Bicycle(orbea, withId(new BicycleType("Road Bike", "x", null, null, null), 1),
                null, "Avant", null), 20);
        when(bicycleService.findById(20)).thenReturn(Optional.of(avant));

        mockMvc.perform(get("/compare").param("a", "20").param("b", "6"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<td class=\"value-missing\">Not confirmed</td>")))
                .andExpect(content().string(containsString("<td>Carbon Fibre</td>")));
    }

    @Test
    void differingSpecificationsAreMarkedAndCounted() throws Exception {
        // touring vs road: brand and wheel size are the same, the touring weight is missing,
        // and the other seven specifications differ
        String html = mockMvc.perform(get("/compare").param("a", "1").param("b", "6"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("7 of 10")))
                .andExpect(content().string(containsString("1 row cannot be compared because a detail is missing.")))
                .andReturn().getResponse().getContentAsString();

        // Counting trick: splitting a text on a marker that appears N times gives N + 1 pieces
        // (-1 keeps empty pieces at the end), so this checks that exactly 7 rows are marked as different
        assertThat(html.split("class=\"row-different\"", -1)).hasSize(7 + 1);
        // The "Differs" label is text, so the difference is not shown by colour alone
        assertThat(html.split("<span class=\"diff-badge\">Differs</span>", -1)).hasSize(7 + 1 + 1); // + legend
    }

    // Photos are chosen from the brand and model name in templates/fragments/photos.html, so the names matter here
    @Test
    void comparisonCardsShowEachBicyclesPhotoWithItsCredit() throws Exception {
        Brand cervelo = withId(new Brand("Cervélo", "Canada", null), 26);
        Bicycle s5 = withId(new Bicycle(cervelo, withId(new BicycleType("Road Bike", "x", null, null, null), 1),
                null, "S5", 2019), 17);
        ReflectionTestUtils.setField(s5, "intendedUse", "Road racing and fast rides on smooth roads.");
        Brand brompton = withId(new Brand("Brompton", "United Kingdom", null), 27);
        Bicycle folding = withId(new Bicycle(brompton, withId(new BicycleType("Folding Bike", "x", null, null, null), 9),
                null, "Folding Bicycle", null), 18);
        when(bicycleService.findById(17)).thenReturn(Optional.of(s5));
        when(bicycleService.findById(18)).thenReturn(Optional.of(folding));

        mockMvc.perform(get("/compare").param("a", "18").param("b", "17"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/images/photos/bike-brompton-folding-1280.webp")))
                .andExpect(content().string(containsString("/images/photos/bike-cervelo-s5-1280.webp")))
                // Attribution stays with each photo
                .andExpect(content().string(containsString("Alicjajoanna")))
                .andExpect(content().string(containsString("This adapted version is licensed under CC BY-SA 4.0.")))
                .andExpect(content().string(containsString("Glory Cycles")))
                .andExpect(content().string(containsString("href=\"https://creativecommons.org/licenses/by/2.0/\"")))
                .andExpect(content().string(containsString("Road racing and fast rides on smooth roads.")))
                .andExpect(content().string(not(containsString("placeholder-bike.svg"))));
    }

    @Test
    void bicyclesWithoutAPhotoKeepThePlaceholderInTheirCard() throws Exception {
        // The Trek test bicycles have no approved photo
        mockMvc.perform(get("/compare").param("a", "1").param("b", "6"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("placeholder-bike.svg")));
    }
}
