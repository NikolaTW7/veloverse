package com.veloverse.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
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

import com.veloverse.model.Bicycle;
import com.veloverse.model.BicycleType;
import com.veloverse.model.Brand;
import com.veloverse.model.Material;
import com.veloverse.service.BicycleService;
import com.veloverse.service.BrandService;

/**
 * Web-layer tests for the Brands list and detail pages, with the service mocked.
 */
@WebMvcTest(BrandController.class)
class BrandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BrandService brandService;

    @MockitoBean
    private BicycleService bicycleService;

    private static Brand trek() {
        Brand trek = new Brand("Trek", "United States", "An American bicycle company.");
        ReflectionTestUtils.setField(trek, "id", 1);
        return trek;
    }

    @Test
    void listPageShowsBrandsWithLearnMoreLinks() throws Exception {
        when(brandService.findAll()).thenReturn(List.of(trek()));

        mockMvc.perform(get("/brands"))
                .andExpect(status().isOk())
                .andExpect(view().name("brands"))
                .andExpect(content().string(containsString("Trek")))
                .andExpect(content().string(containsString("United States")))
                .andExpect(content().string(containsString("An American bicycle company.")))
                .andExpect(content().string(containsString("href=\"/brands/1\"")));
    }

    @Test
    void listPageShowsEmptyStateWhenThereAreNoBrands() throws Exception {
        when(brandService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/brands"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("No brands have been added yet.")));
    }

    @Test
    void detailPageShowsNameCountryDescriptionAndBackLink() throws Exception {
        when(brandService.findById(1)).thenReturn(Optional.of(trek()));
        when(bicycleService.findByBrand(1)).thenReturn(List.of());

        mockMvc.perform(get("/brands/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("brand-detail"))
                .andExpect(content().string(containsString("Trek")))
                .andExpect(content().string(containsString("United States")))
                .andExpect(content().string(containsString("An American bicycle company.")))
                .andExpect(content().string(containsString("Back to all brands")))
                .andExpect(content().string(containsString("href=\"/brands\"")));
    }

    @Test
    void unknownBrandIdReturnsNotFound() throws Exception {
        when(brandService.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/brands/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void nonNumericBrandIdReturnsNotFound() throws Exception {
        mockMvc.perform(get("/brands/abc"))
                .andExpect(status().isNotFound());
    }

    @Test
    void detailPageListsTheBrandsBicycleModelsWithLinks() throws Exception {
        Brand trek = trek();
        BicycleType road = new BicycleType("Road Bike", "Fast on paved roads.", null, null, null);
        Material carbon = new Material("Carbon Fibre", "Light and stiff.");
        Bicycle domane = new Bicycle(trek, road, carbon, "Domane SL 5 Gen 4", 2024);
        ReflectionTestUtils.setField(domane, "id", 6);
        when(brandService.findById(1)).thenReturn(Optional.of(trek));
        when(bicycleService.findByBrand(1)).thenReturn(List.of(domane));

        mockMvc.perform(get("/brands/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Trek Models")))
                .andExpect(content().string(containsString("Domane SL 5 Gen 4")))
                .andExpect(content().string(containsString("Road Bike")))
                .andExpect(content().string(containsString("Carbon Fibre")))
                .andExpect(content().string(containsString("href=\"/bicycles/6\"")))
                .andExpect(content().string(not(containsString("have been added yet"))));
    }

    @Test
    void detailPageShowsEmptyStateWhenTheBrandHasNoBicycles() throws Exception {
        when(brandService.findById(1)).thenReturn(Optional.of(trek()));
        when(bicycleService.findByBrand(1)).thenReturn(List.of());

        mockMvc.perform(get("/brands/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("No Trek bicycle models have been added yet.")));
    }
}
