package com.veloverse.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.veloverse.model.Bicycle;
import com.veloverse.model.BicycleType;
import com.veloverse.model.Brand;
import com.veloverse.model.Material;
import com.veloverse.repository.BicycleRepository;

@Service
@Transactional(readOnly = true)
public class BicycleService {

    private final BicycleRepository repository;

    public BicycleService(BicycleRepository repository) {
        this.repository = repository;
    }

    /** All bicycles by ID, with brand, type and frame material already loaded for the page. */
    public List<Bicycle> findAll() {
        return repository.findAllByOrderByIdAsc();
    }

    /**
     * Bicycles whose model name, brand or type contains the search text (case-insensitive).
     * An empty or blank search returns every bicycle.
     */
    public List<Bicycle> search(String query) {
        if (query == null || query.isBlank()) {
            return findAll();
        }
        String text = query.trim();
        return repository
                .findByNameContainingIgnoreCaseOrBrandNameContainingIgnoreCaseOrTypeNameContainingIgnoreCaseOrderByIdAsc(
                        text, text, text);
    }

    /**
     * Search plus the three dropdown filters. A null ID means "All" for that filter,
     * and every filter that is set must match, so filters can be combined.
     * The catalogue is small, so the filters are applied in Java to the search results;
     * this keeps the existing search query unchanged.
     */
    public List<Bicycle> search(String query, Integer typeId, Integer brandId, Integer materialId) {
        return search(query).stream()
                .filter(bike -> typeId == null || typeId.equals(bike.getType().getId()))
                .filter(bike -> brandId == null || brandId.equals(bike.getBrand().getId()))
                .filter(bike -> materialId == null || materialId.equals(bike.getFrameMaterial().getId()))
                .toList();
    }

    /** Bicycle types that at least one bicycle uses, A to Z (for the Type filter). */
    public List<BicycleType> findTypesInUse() {
        return repository.findTypesInUse();
    }

    /** Brands that have at least one bicycle, A to Z (for the Brand filter). */
    public List<Brand> findBrandsInUse() {
        return repository.findBrandsInUse();
    }

    /** Frame materials that at least one bicycle uses, A to Z (for the Frame Material filter). */
    public List<Material> findFrameMaterialsInUse() {
        return repository.findFrameMaterialsInUse();
    }

    /** All bicycles made by one brand, by ID. Empty if the brand has no bicycles yet. */
    public List<Bicycle> findByBrand(Integer brandId) {
        return repository.findByBrandIdOrderByIdAsc(brandId);
    }

    /** One bicycle with its brand, type and frame material, or an empty Optional if no bicycle has this ID. */
    public Optional<Bicycle> findById(Integer id) {
        return repository.findWithDetailsById(id);
    }
}
