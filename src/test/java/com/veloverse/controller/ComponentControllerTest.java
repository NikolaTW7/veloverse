package com.veloverse.controller;

// Static imports let the tests call these methods without their class name:
// get builds a fake GET request, status / view / content check the response,
// containsString (Hamcrest) checks the page text, and when (Mockito) sets what a mock returns.
// not(...) turns a check around, and any() (Mockito) matches any argument when stubbing.
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.veloverse.model.Component;
import com.veloverse.model.ComponentSystem;
import com.veloverse.model.Material;
import com.veloverse.service.ComponentService;

/**
 * Web-layer tests for the Components & Anatomy pages (FR-02), with the service mocked.
 * @WebMvcTest starts only the web layer (this controller plus Thymeleaf), not the services or the database.
 * Each test follows arrange (tell the mock service what to return), act (send a fake request with
 * MockMvc) and assert (check the status, the template name and the rendered HTML).
 */
@WebMvcTest(ComponentController.class)
class ComponentControllerTest {

    // MockMvc sends fake HTTP requests to the controller without starting a real web server.
    // @Autowired asks Spring to put its MockMvc object into this field.
    @Autowired
    private MockMvc mockMvc;

    // @MockitoBean: Spring gives the controller a Mockito mock in place of the real service.
    // A mock returns only what a test sets with when(...), otherwise an empty list, an empty Optional or null.
    @MockitoBean
    private ComponentService componentService;

    // Shared test data: the Drivetrain system with two of its components.
    // JUnit creates a new test object for every test, so changes a test makes (e.g. adding materials) do not leak into other tests.
    private final ComponentSystem drivetrain = withId(
            new ComponentSystem("Drivetrain", "Transfers power to the rear wheel."), 2);
    private final Component chain = withId(
            new Component(drivetrain, "Chain", "Linked plates and rollers.", "Transfers power.", "Pulled by the chainring."), 4);
    private final Component cassette = withId(
            new Component(drivetrain, "Cassette", "A cluster of sprockets.", "Gives a range of gears.", "The derailleur moves the chain."), 5);

    /**
     * Gives a test entity an ID and returns it.
     * The entities have no setters (the database normally sets the ID), so ReflectionTestUtils.setField
     * writes the private "id" field directly. Generic <T> means it works for both ComponentSystem and Component.
     */
    private static <T> T withId(T entity, int id) {
        ReflectionTestUtils.setField(entity, "id", id);
        return entity;
    }

    @Test
    void overviewShowsSystemsAndLinksToComponents() throws Exception {
        // Arrange: put both components into the system, as JPA would when loading it from the database
        drivetrain.getComponents().addAll(List.of(chain, cassette));
        when(componentService.findAllSystems()).thenReturn(List.of(drivetrain));

        mockMvc.perform(get("/components"))
                .andExpect(status().isOk())
                .andExpect(view().name("components/overview"))
                .andExpect(content().string(containsString("Drivetrain")))
                .andExpect(content().string(containsString("id=\"system-2\"")))
                .andExpect(content().string(containsString("href=\"/components/5\"")));
    }

    @Test
    void overviewShowsEmptyStateWhenThereAreNoSystems() throws Exception {
        when(componentService.findAllSystems()).thenReturn(List.of());

        mockMvc.perform(get("/components"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("No components have been added yet.")));
    }

    @Test
    void detailShowsPurposeOperationMaterialsAndSystem() throws Exception {
        cassette.getMaterials().add(new Material("Steel", "Strong and durable."));
        when(componentService.findById(5)).thenReturn(Optional.of(cassette));
        when(componentService.findSystemComponents(any())).thenReturn(List.of(chain, cassette));

        mockMvc.perform(get("/components/5"))
                .andExpect(status().isOk())
                .andExpect(view().name("components/detail"))
                .andExpect(content().string(containsString("Cassette")))
                .andExpect(content().string(containsString("Gives a range of gears.")))
                .andExpect(content().string(containsString("The derailleur moves the chain.")))
                .andExpect(content().string(containsString("Steel")))
                .andExpect(content().string(containsString("Transfers power to the rear wheel.")))
                // The other component in the same system is linked as related
                .andExpect(content().string(containsString("href=\"/components/4\"")));
    }

    @Test
    void detailShowsEmptyStateWhenNoMaterialsAreRecorded() throws Exception {
        when(componentService.findById(4)).thenReturn(Optional.of(chain));
        when(componentService.findSystemComponents(any())).thenReturn(List.of(chain));

        mockMvc.perform(get("/components/4"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("No materials have been recorded for this component yet.")));
    }

    // The service finds nothing (empty Optional), so the controller answers 404 Not Found
    @Test
    void unknownComponentIdReturnsNotFound() throws Exception {
        when(componentService.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/components/999"))
                .andExpect(status().isNotFound());
    }

    // The URL pattern only accepts digits, so "abc" matches no controller method and gives 404 instead of a crash
    @Test
    void nonNumericComponentIdReturnsNotFound() throws Exception {
        mockMvc.perform(get("/components/abc"))
                .andExpect(status().isNotFound());
    }

    @Test
    void stemCaliperAndSeatpostNowShowTheirPhotosInsteadOfThePlaceholder() throws Exception {
        // Photos are chosen from the component name in templates/fragments/photos.html, so the names matter here.
        // The test checks the overview page first, then the stem detail page.
        ComponentSystem cockpit = withId(new ComponentSystem("Cockpit / Steering", "Steering parts."), 5);
        Component stem = withId(new Component(cockpit, "Stem", "Clamps the handlebar.", "Connects the bar.", "Clamps the steerer."), 14);
        ComponentSystem braking = withId(new ComponentSystem("Braking System", "Stopping parts."), 3);
        Component caliper = withId(new Component(braking, "Brake Caliper", "Holds the pads.", "Squeezes the pads.", "Pistons move."), 8);
        ComponentSystem seating = withId(new ComponentSystem("Seating", "Saddle parts."), 6);
        Component seatpost = withId(new Component(seating, "Seatpost", "Holds the saddle.", "Sets saddle height.", "Clamped in the seat tube."), 16);
        cockpit.getComponents().add(stem);
        braking.getComponents().add(caliper);
        seating.getComponents().add(seatpost);
        when(componentService.findAllSystems()).thenReturn(List.of(braking, cockpit, seating));

        mockMvc.perform(get("/components"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/images/photos/part-stem-640.webp")))
                .andExpect(content().string(containsString("/images/photos/part-brake-caliper-640.webp")))
                .andExpect(content().string(containsString("/images/photos/part-seatpost-640.webp")))
                .andExpect(content().string(not(containsString("placeholder-bike.svg"))));

        when(componentService.findById(14)).thenReturn(Optional.of(stem));
        when(componentService.findSystemComponents(any())).thenReturn(List.of(stem));

        mockMvc.perform(get("/components/14"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/images/photos/part-stem-1280.webp")))
                .andExpect(content().string(not(containsString("placeholder-bike.svg"))));
    }

    // "Dropout" (ID 99) is a test component with no photo, so the placeholder image is shown
    @Test
    void componentWithoutAPhotoKeepsThePlaceholder() throws Exception {
        ComponentSystem frameSystem = withId(new ComponentSystem("Frame System", "The backbone."), 1);
        Component dropout = withId(new Component(frameSystem, "Dropout", "Holds the axle.", "Holds the wheel.", "Slots for the axle."), 99);
        when(componentService.findById(99)).thenReturn(Optional.of(dropout));
        when(componentService.findSystemComponents(any())).thenReturn(List.of(dropout));

        mockMvc.perform(get("/components/99"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("placeholder-bike.svg")));
    }

    // Each material card links to that material's own page instead of the old "coming soon" text.
    @Test
    void materialCardsLinkToTheMaterialPages() throws Exception {
        cassette.getMaterials().add(withId(new Material("Steel", "Strong and durable."), 3));
        when(componentService.findById(5)).thenReturn(Optional.of(cassette));
        when(componentService.findSystemComponents(any())).thenReturn(List.of(cassette));

        mockMvc.perform(get("/components/5"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("href=\"/materials/3\"")))
                .andExpect(content().string(not(containsString("Material page coming soon"))));
    }
}
