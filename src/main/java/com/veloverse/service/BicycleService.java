package com.veloverse.service;

// Normalizer and Locale are used to ignore accents and letter case when searching
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.veloverse.model.Bicycle;
import com.veloverse.model.BicycleType;
import com.veloverse.model.Brand;
import com.veloverse.model.Material;
import com.veloverse.repository.BicycleRepository;

/**
 * Application logic for bicycles: listing, searching, filtering and looking up one bicycle.
 * Controllers call this service, and this service calls BicycleRepository to read the database.
 */
// @Service tells Spring to create one shared object of this class and inject it where it is needed.
// @Transactional(readOnly = true) runs every public method inside a read-only database transaction.
// Lazy relationships can only be loaded while the transaction is open, and the project sets
// spring.jpa.open-in-view=false, so all data a page needs is loaded here, before the template runs.
// readOnly = true also tells Hibernate it does not need to look for changes to save.
@Service
@Transactional(readOnly = true)
public class BicycleService {

    // Constructor injection: Spring passes in the repository. private final means it is set once and never replaced.
    private final BicycleRepository repository;

    public BicycleService(BicycleRepository repository) {
        this.repository = repository;
    }

    /** All bicycles by ID, with brand, type and frame material already loaded for the page. */
    public List<Bicycle> findAll() {
        return repository.findAllByOrderByIdAsc();
    }

    /**
     * Bicycles whose model name, brand or type contains the search text.
     * Letter case and accents are ignored, so "cervelo" finds "Cervélo".
     * An empty or blank search returns every bicycle.
     * The catalogue is small, so the matching is done in Java on the full list.
     */
    public List<Bicycle> search(String query) {
        List<Bicycle> all = findAll();
        if (query == null || query.isBlank()) {
            return all;
        }
        String text = simplify(query.trim());
        // filter keeps only the bicycles for which the condition is true
        return all.stream()
                .filter(bike -> simplify(bike.getName()).contains(text)
                        || simplify(bike.getBrand().getName()).contains(text)
                        || simplify(bike.getType().getName()).contains(text))
                .toList();
    }

    /** Lower case without accents, e.g. "Cervélo" becomes "cervelo". */
    // NFD splits "é" into "e" plus a separate accent mark; \p{M} then removes all such marks.
    private static String simplify(String text) {
        String withoutAccents = Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return withoutAccents.toLowerCase(Locale.ROOT);
    }

    /**
     * Search plus the three dropdown filters. A null ID means "All" for that filter,
     * and every filter that is set must match, so filters can be combined.
     * The filters are applied in Java to the search results.
     */
    public List<Bicycle> search(String query, Integer typeId, Integer brandId, Integer materialId) {
        return search(query).stream()
                .filter(bike -> typeId == null || typeId.equals(bike.getType().getId()))
                .filter(bike -> brandId == null || brandId.equals(bike.getBrand().getId()))
                // Bicycles without a confirmed frame material never match a material filter
                .filter(bike -> materialId == null
                        || (bike.getFrameMaterial() != null && materialId.equals(bike.getFrameMaterial().getId())))
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

    /**
     * One bicycle with its brand, type and frame material, or an empty Optional if no bicycle has this ID.
     * An Optional is a container that holds either one value or nothing; it makes the caller deal
     * with the "not found" case instead of getting a null by surprise.
     */
    public Optional<Bicycle> findById(Integer id) {
        return repository.findWithDetailsById(id);
    }
}
