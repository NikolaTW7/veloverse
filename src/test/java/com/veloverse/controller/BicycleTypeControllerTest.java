package com.veloverse.controller;

// Static imports let the tests call these methods without their class name:
// get builds a fake GET request, status / view / content check the response,
// containsString (Hamcrest) checks the page text, and when (Mockito) sets what a mock returns.
// any() and anyInt() (Mockito) match any argument when stubbing a method.
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

import com.veloverse.model.BicycleType;
import com.veloverse.service.BicycleTypeService;

/**
 * Web-layer tests for the Bicycle Types pages (FR-01), with the service mocked.
 * @WebMvcTest starts only the web layer (this controller plus Thymeleaf), not the services or the database.
 * Each test follows arrange (tell the mock service what to return), act (send a fake request with
 * MockMvc) and assert (check the status, the template name and the rendered HTML).
 */
@WebMvcTest(BicycleTypeController.class)
class BicycleTypeControllerTest {

    // MockMvc sends fake HTTP requests to the controller without starting a real web server.
    // @Autowired asks Spring to put its MockMvc object into this field.
    @Autowired
    private MockMvc mockMvc;

    // @MockitoBean: Spring gives the controller a Mockito mock in place of the real service.
    // A mock returns only what a test sets with when(...), otherwise an empty list, an empty Optional or null.
    @MockitoBean
    private BicycleTypeService bicycleTypeService;

    /**
     * Builds a test bicycle type with this ID and name; "\n" separates the two advantages.
     * BicycleType has no setters (the database normally sets the ID), so ReflectionTestUtils.setField
     * writes the private "id" field directly.
     */
    private static BicycleType type(int id, String name) {
        BicycleType type = new BicycleType(name, name + " description.", name + " use",
                "First advantage\nSecond advantage", "Only disadvantage");
        ReflectionTestUtils.setField(type, "id", id);
        return type;
    }

    @Test
    void listPageShowsAllTypesWithLinks() throws Exception {
        when(bicycleTypeService.findAll()).thenReturn(List.of(type(1, "Road Bike"), type(2, "Mountain Bike")));

        mockMvc.perform(get("/bicycle-types"))
                .andExpect(status().isOk())
                .andExpect(view().name("bicycle-types/list"))
                .andExpect(content().string(containsString("Road Bike")))
                .andExpect(content().string(containsString("Mountain Bike")))
                .andExpect(content().string(containsString("href=\"/bicycle-types/2\"")));
    }

    @Test
    void listPageShowsEmptyStateWhenThereAreNoTypes() throws Exception {
        when(bicycleTypeService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/bicycle-types"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("No bicycle types have been added yet.")));
    }

    @Test
    void detailPageShowsTypeAdvantagesAndRelatedTypes() throws Exception {
        BicycleType road = type(1, "Road Bike");
        when(bicycleTypeService.findById(1)).thenReturn(Optional.of(road));
        // Whatever arguments the controller passes, the related types section gets Gravel Bike (ID 3)
        when(bicycleTypeService.findOthers(any(), anyInt())).thenReturn(List.of(type(3, "Gravel Bike")));

        mockMvc.perform(get("/bicycle-types/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("bicycle-types/detail"))
                .andExpect(content().string(containsString("Road Bike")))
                .andExpect(content().string(containsString("Second advantage")))
                .andExpect(content().string(containsString("Only disadvantage")))
                .andExpect(content().string(containsString("href=\"/bicycle-types/3\"")));
    }

    // The service finds nothing (empty Optional), so the controller answers 404 Not Found
    @Test
    void unknownTypeIdReturnsNotFound() throws Exception {
        when(bicycleTypeService.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/bicycle-types/999"))
                .andExpect(status().isNotFound());
    }

    // The URL pattern only accepts digits, so "abc" matches no controller method and gives 404 instead of a crash
    @Test
    void nonNumericTypeIdReturnsNotFound() throws Exception {
        mockMvc.perform(get("/bicycle-types/abc"))
                .andExpect(status().isNotFound());
    }

    // The list card for this type shows its CC BY-SA 4.0 photo with a licence link and a note about the changes
    @Test
    void timeTrialTypeShowsItsLicensedPhoto() throws Exception {
        when(bicycleTypeService.findAll()).thenReturn(List.of(type(10, "Time-Trial / Triathlon Bike")));

        mockMvc.perform(get("/bicycle-types"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Time-Trial / Triathlon Bike")))
                .andExpect(content().string(containsString("/images/photos/bike-canyon-speedmax-cfr-640.webp")))
                .andExpect(content().string(containsString("href=\"https://creativecommons.org/licenses/by-sa/4.0/\"")))
                .andExpect(content().string(containsString("This adapted version is licensed under CC BY-SA 4.0.")));
    }
}
