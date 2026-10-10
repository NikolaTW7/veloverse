package com.veloverse.controller;

// Static imports let the tests call these methods without their class name:
// containsString / not (Hamcrest) check the page text, when (Mockito) sets what a mock returns,
// get builds a fake GET request, and status / view / content check the response.
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

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
import com.veloverse.service.BicycleTypeService;

/**
 * Web-layer test for the Home page.
 * @WebMvcTest loads only the MVC and Thymeleaf parts; the service is mocked, so no database is needed.
 * Each test follows arrange (tell the mocks what to return), act (send a fake request with MockMvc)
 * and assert (check the status, the template name and the rendered HTML).
 */
@WebMvcTest(HomeController.class)
class HomeControllerTest {

    // MockMvc sends fake HTTP requests to the controller without starting a real web server.
    // @Autowired asks Spring to put its MockMvc object into this field.
    @Autowired
    private MockMvc mockMvc;

    // @MockitoBean: Spring uses a Mockito mock in place of the real service, so HomeController gets the fake.
    // A mock that was not told what to return gives an empty list for methods that return a List.
    @MockitoBean
    private BicycleTypeService bicycleTypeService;

    @MockitoBean
    private BicycleService bicycleService;

    // "throws Exception" is needed because mockMvc.perform(...) can throw a checked exception
    @Test
    void homePageReturnsOkAndRendersHomeTemplate() throws Exception {
        // Entities have no setters, so ReflectionTestUtils.setField writes the private id field directly
        BicycleType road = new BicycleType("Road Bike", "Fast on paved roads.", "Road cycling", "", "");
        ReflectionTestUtils.setField(road, "id", 1);
        when(bicycleTypeService.findAll()).thenReturn(List.of(road));

        // Act and assert: request the home page, then expect HTTP 200 and the home.html template
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                // Content from the page itself and from the shared header and footer
                .andExpect(content().string(containsString("Understand")))
                .andExpect(content().string(containsString("What to Explore")))
                .andExpect(content().string(containsString("Main navigation")))
                .andExpect(content().string(containsString("Educational project")))
                // Bicycle types now come from the service and link to their detail pages
                .andExpect(content().string(containsString("Road Bike")))
                .andExpect(content().string(containsString("href=\"/bicycle-types/1\"")))
                // The Components section is now a real link
                .andExpect(content().string(containsString("href=\"/components\"")));
    }

    @Test
    void homePageShowsEmptyStateWhenThereAreNoTypes() throws Exception {
        when(bicycleTypeService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("No bicycle types have been added yet.")));
    }

    @Test
    void bicyclesLinksAreEnabledAndFeaturedModelsComeFromTheDatabase() throws Exception {
        BicycleType gravel = new BicycleType("Gravel Bike", "Mixed surfaces.", null, null, null);
        Material aluminium = new Material("Aluminium", "Light and affordable.");
        Bicycle topstone = new Bicycle(new Brand("Cannondale", null, null), gravel, aluminium, "Topstone 3", 2024);
        ReflectionTestUtils.setField(topstone, "id", 4);
        when(bicycleTypeService.findAll()).thenReturn(List.of());
        when(bicycleService.findAll()).thenReturn(List.of(topstone));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                // Header, footer, Explore Bicycles button and View all link all point to /bicycles
                .andExpect(content().string(containsString("href=\"/bicycles\"")))
                .andExpect(content().string(not(containsString("Explore Bicycles <small>Coming soon</small>"))))
                .andExpect(content().string(not(containsString("All bicycles coming soon"))))
                // Featured Models shows the real bicycle with a link to its detail page
                .andExpect(content().string(containsString("Cannondale")))
                .andExpect(content().string(containsString("Topstone 3")))
                .andExpect(content().string(containsString("Gravel Bike · Aluminium · 2024")))
                .andExpect(content().string(containsString("href=\"/bicycles/4\"")))
                .andExpect(content().string(not(containsString("Model to be added"))));
    }

    @Test
    void featuredModelsShowsEmptyStateWhenThereAreNoBicycles() throws Exception {
        when(bicycleTypeService.findAll()).thenReturn(List.of());
        when(bicycleService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("No bicycles have been added yet.")));
    }
}
