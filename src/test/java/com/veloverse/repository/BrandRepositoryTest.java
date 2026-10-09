package com.veloverse.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.veloverse.model.Brand;
import com.veloverse.service.BrandService;

/**
 * Checks against the real PostgreSQL database that the brands table was created and filled.
 * Like the other database tests, this needs DB_PASSWORD to be set. It only reads data.
 */
@SpringBootTest
class BrandRepositoryTest {

    @Autowired
    private BrandService brandService;

    @Test
    void starterBrandsAreLoadedInOrderWithCountries() {
        List<Brand> brands = brandService.findAll();

        assertThat(brands).extracting(Brand::getName)
                .contains("Trek", "Specialized", "Giant", "Cannondale", "Brompton");
        assertThat(brands).extracting(Brand::getId).isSorted();
        assertThat(brands).allSatisfy(brand -> assertThat(brand.getCountry()).isNotBlank());
    }
}
