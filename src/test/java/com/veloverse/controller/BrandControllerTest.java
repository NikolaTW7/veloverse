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
 * @WebMvcTest starts only the web layer (this controller plus Thymeleaf), not the services or the database.
 * Each test follows arrange (tell the mock service what to return), act (send a fake request with
 * MockMvc) and assert (check the status, the template name and the rendered HTML).
 */
@WebMvcTest(BrandController.class)
class BrandControllerTest {

    // MockMvc sends fake HTTP requests to the controller without starting a real web server.
    // @Autowired asks Spring to put its MockMvc object into this field.
    @Autowired
    private MockMvc mockMvc;

    // @MockitoBean: Spring gives the controller a Mockito mock in place of the real service.
    // A mock returns only what a test sets with when(...), otherwise an empty list, an empty Optional or null.
    @MockitoBean
    private BrandService brandService;

    @MockitoBean
    private BicycleService bicycleService;

    /**
     * Builds a test brand "Trek" with ID 1.
     * Brand has no setters (the database normally sets the ID), so ReflectionTestUtils.setField
     * writes the private "id" field directly. Later tests use it the same way for other private fields.
     */
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

    // The service finds nothing (empty Optional), so the controller answers 404 Not Found
    @Test
    void unknownBrandIdReturnsNotFound() throws Exception {
        when(brandService.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/brands/999"))
                .andExpect(status().isNotFound());
    }

    // The URL pattern only accepts digits, so "abc" matches no controller method and gives 404 instead of a crash
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
                // The empty-state message must not appear when the brand has a bicycle
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

    // Tsunami has no country (null), so the page shows "Not stated by the brand";
    // the specialities text is split on "\n" into the Known For list
    @Test
    void detailPageShowsWebsiteSpecialitiesAndUnknownCountry() throws Exception {
        Brand tsunami = new Brand("Tsunami", null, "A brand of track and fixed-gear bikes.");
        ReflectionTestUtils.setField(tsunami, "id", 3);
        ReflectionTestUtils.setField(tsunami, "website", "https://tsunamibicycle.com");
        ReflectionTestUtils.setField(tsunami, "specialities", "Track and fixed-gear bikes\nSingle-speed bikes");
        when(brandService.findById(3)).thenReturn(Optional.of(tsunami));
        when(bicycleService.findByBrand(3)).thenReturn(List.of());

        mockMvc.perform(get("/brands/3"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Not stated by the brand")))
                .andExpect(content().string(containsString("href=\"https://tsunamibicycle.com\"")))
                .andExpect(content().string(containsString("Official website")))
                .andExpect(content().string(containsString("Known For")))
                .andExpect(content().string(containsString("Single-speed bikes")));
    }

    // Checks the Colnago photo and licence link on the list page, then the large photo and its credit on the detail page
    @Test
    void brandPagesShowTheBrandsLicensedPhoto() throws Exception {
        Brand colnago = new Brand("Colnago", "Italy", "An Italian bicycle company.");
        ReflectionTestUtils.setField(colnago, "id", 28);
        when(brandService.findAll()).thenReturn(List.of(colnago));
        when(brandService.findById(28)).thenReturn(Optional.of(colnago));
        when(bicycleService.findByBrand(28)).thenReturn(List.of());

        mockMvc.perform(get("/brands"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/images/photos/bike-colnago-v3rs-640.webp")))
                .andExpect(content().string(containsString("href=\"https://creativecommons.org/licenses/by/2.0/\"")));

        mockMvc.perform(get("/brands/28"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/images/photos/bike-colnago-v3rs-1280.webp")))
                .andExpect(content().string(containsString("Glory Cycles")))
                .andExpect(content().string(containsString("Cropped and resized by VeloVerse.")));
    }

    // The Avant is created with a null frame material, so its card shows "Not confirmed" instead of failing
    @Test
    void modelWithoutAConfirmedFrameMaterialShowsNotConfirmed() throws Exception {
        Brand orbea = new Brand("Orbea", "Spain", "A Spanish bicycle company.");
        ReflectionTestUtils.setField(orbea, "id", 29);
        BicycleType road = new BicycleType("Road Bike", "Fast on paved roads.", null, null, null);
        Bicycle avant = new Bicycle(orbea, road, null, "Avant", null);
        ReflectionTestUtils.setField(avant, "id", 20);
        when(brandService.findById(29)).thenReturn(Optional.of(orbea));
        when(bicycleService.findByBrand(29)).thenReturn(List.of(avant));

        mockMvc.perform(get("/brands/29"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Avant")))
                .andExpect(content().string(containsString("Not confirmed")))
                .andExpect(content().string(containsString("/images/photos/bike-orbea-avant-640.webp")));
    }
}
