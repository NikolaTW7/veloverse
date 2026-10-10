package com.veloverse.controller;

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
 */
@WebMvcTest(CompareController.class)
class CompareControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BicycleService bicycleService;

    private Bicycle touring;
    private Bicycle road;

    private static <T> T withId(T entity, int id) {
        ReflectionTestUtils.setField(entity, "id", id);
        return entity;
    }

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
                .andExpect(content().string(containsString("<td>Not specified</td>")))
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
                .andExpect(content().string(containsString("<td>Not confirmed</td>")))
                .andExpect(content().string(containsString("<td>Carbon Fibre</td>")));
    }
}
