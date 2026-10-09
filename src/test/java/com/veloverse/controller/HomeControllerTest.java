package com.veloverse.controller;

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
 */
@WebMvcTest(HomeController.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BicycleTypeService bicycleTypeService;

    @MockitoBean
    private BicycleService bicycleService;

    @Test
    void homePageReturnsOkAndRendersHomeTemplate() throws Exception {
        BicycleType road = new BicycleType("Road Bike", "Fast on paved roads.", "Road cycling", "", "");
        ReflectionTestUtils.setField(road, "id", 1);
        when(bicycleTypeService.findAll()).thenReturn(List.of(road));

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
