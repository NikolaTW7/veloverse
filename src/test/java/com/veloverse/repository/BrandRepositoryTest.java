package com.veloverse.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.veloverse.model.Brand;
import com.veloverse.service.BrandService;

/**
 * Checks against the real PostgreSQL database that the five brands are loaded with their details.
 * Like the other database tests, this needs DB_PASSWORD to be set. It runs against the separate
 * veloverse_test database (see src/test/resources/config/application.properties), which is
 * rebuilt from schema.sql and data.sql for every test run, so no update script is needed.
 */
@SpringBootTest
class BrandRepositoryTest {

    @Autowired
    private BrandService brandService;

    private Brand find(List<Brand> brands, String name) {
        return brands.stream()
                .filter(brand -> brand.getName().equals(name))
                .findFirst()
                .orElseThrow();
    }

    @Test
    void exactlyTheFiveApprovedBrandsAreLoadedInOrder() {
        List<Brand> brands = brandService.findAll();

        assertThat(brands).extracting(Brand::getName)
                .containsExactlyInAnyOrder("Cervélo", "Brompton", "Colnago", "Orbea", "Canyon");
        assertThat(brands).extracting(Brand::getId).isSorted();
    }

    @Test
    void everyBrandHasACountryWebsiteDescriptionAndSpecialities() {
        assertThat(brandService.findAll()).allSatisfy(brand -> {
            assertThat(brand.getCountry()).isNotBlank();
            assertThat(brand.getWebsite()).startsWith("https://");
            assertThat(brand.getDescription()).isNotBlank();
            assertThat(brand.getSpecialityList()).isNotEmpty();
        });
    }

    @Test
    void brandsHaveTheirCorrectCountries() {
        List<Brand> brands = brandService.findAll();

        assertThat(find(brands, "Cervélo").getCountry()).isEqualTo("Canada");
        assertThat(find(brands, "Brompton").getCountry()).isEqualTo("United Kingdom");
        assertThat(find(brands, "Colnago").getCountry()).isEqualTo("Italy");
        assertThat(find(brands, "Orbea").getCountry()).isEqualTo("Spain");
        assertThat(find(brands, "Canyon").getCountry()).isEqualTo("Germany");
    }
}
