package com.veloverse.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.veloverse.model.Component;
import com.veloverse.model.ComponentSystem;
import com.veloverse.repository.ComponentRepository;
import com.veloverse.repository.ComponentSystemRepository;

/**
 * Application logic for component systems and components, used by ComponentController.
 */
// @Service: Spring creates one shared object of this class.
// @Transactional(readOnly = true): each public method runs in a read-only database transaction,
// so the lazy relationships requested with @EntityGraph are loaded before the page is rendered.
@Service
@Transactional(readOnly = true)
public class ComponentService {

    // Two repositories, both set once by the constructor (constructor injection)
    private final ComponentSystemRepository systemRepository;
    private final ComponentRepository componentRepository;

    public ComponentService(ComponentSystemRepository systemRepository,
                            ComponentRepository componentRepository) {
        this.systemRepository = systemRepository;
        this.componentRepository = componentRepository;
    }

    /** All systems, each with its components, in display order. */
    public List<ComponentSystem> findAllSystems() {
        return systemRepository.findAllByOrderByIdAsc();
    }

    /** A component with its system and materials loaded, ready for the detail page. */
    public Optional<Component> findById(Integer id) {
        return componentRepository.findWithDetailsById(id);
    }

    /** Every component in the same system as the given one, including itself. */
    public List<Component> findSystemComponents(Component component) {
        return componentRepository.findBySystemIdOrderByIdAsc(component.getSystem().getId());
    }
}
