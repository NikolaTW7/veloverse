package com.veloverse.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.veloverse.model.Component;
import com.veloverse.model.ComponentSystem;
import com.veloverse.model.Material;
import com.veloverse.service.ComponentService;

/**
 * Checks against the real PostgreSQL database that the component tables were created and filled,
 * and that the relationships (system -> components, component <-> materials) are mapped correctly.
 * Like the other database tests, this needs DB_PASSWORD to be set. It only reads data.
 */
@SpringBootTest
class ComponentRepositoryTest {

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ComponentRepository componentRepository;

    @Test
    void systemsAreLoadedWithTheirComponents() {
        List<ComponentSystem> systems = componentService.findAllSystems();

        assertThat(systems).extracting(ComponentSystem::getName)
                .contains("Frame System", "Drivetrain", "Braking System",
                        "Wheel System", "Cockpit / Steering", "Seating");

        ComponentSystem drivetrain = systems.stream()
                .filter(system -> system.getName().equals("Drivetrain"))
                .findFirst()
                .orElseThrow();
        assertThat(drivetrain.getComponents()).extracting(Component::getName)
                .contains("Crankset", "Chain", "Cassette", "Rear Derailleur");
    }

    @Test
    void componentDetailsIncludeSystemAndMaterials() {
        Component cassette = componentRepository.findAll().stream()
                .filter(component -> component.getName().equals("Cassette"))
                .findFirst()
                .orElseThrow();

        Component loaded = componentService.findById(cassette.getId()).orElseThrow();

        assertThat(loaded.getSystem().getName()).isEqualTo("Drivetrain");
        assertThat(loaded.getPurpose()).isNotBlank();
        assertThat(loaded.getOperation()).isNotBlank();
        assertThat(loaded.getMaterials()).extracting(Material::getName)
                .contains("Steel", "Aluminium");
    }

    @Test
    void systemComponentsIncludeTheComponentItself() {
        Component chain = componentRepository.findAll().stream()
                .filter(component -> component.getName().equals("Chain"))
                .findFirst()
                .orElseThrow();
        Component loaded = componentService.findById(chain.getId()).orElseThrow();

        assertThat(componentService.findSystemComponents(loaded))
                .extracting(Component::getName)
                .contains("Chain", "Cassette");
    }
}
