package com.veloverse.repository;

// Static import: lets the tests write assertThat(...) (from AssertJ) instead of Assertions.assertThat(...)
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
 * @SpringBootTest starts the whole application against the separate veloverse_test database
 * (see src/test/resources/config/application.properties), so nothing is mocked here.
 */
@SpringBootTest
class ComponentRepositoryTest {

    // @Autowired: Spring puts its real service and repository objects into these fields before the tests run
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

    // Arrange: find the Cassette ID first (IDs are not hard-coded). Act: load it through the service.
    // Assert: its system, texts and materials (from the component_materials link table) are loaded.
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

    // findSystemComponents returns every component in the same system, including the one passed in
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
