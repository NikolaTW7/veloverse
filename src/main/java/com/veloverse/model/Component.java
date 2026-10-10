package com.veloverse.model;

import java.util.ArrayList;
import java.util.List;

// JPA annotations that describe how this class and its relationships map to database tables
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

/**
 * An individual bicycle part, such as the Chain or Cassette.
 * Maps to the components table.
 */
// @Entity: a JPA entity, so each object of this class is one row of the table named in @Table.
@Entity
@Table(name = "components")
public class Component {

    // Primary key, numbered automatically by PostgreSQL (IDENTITY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "component_id")
    private Integer id;

    // The system this part belongs to (many components -> one system), using the system_id foreign key.
    // LAZY: the system is only read when needed; ComponentRepository loads it with @EntityGraph.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "system_id")
    private ComponentSystem system;

    private String name;

    // What the component is
    private String description;

    // What the component does
    private String purpose;

    // How the component works
    private String operation;

    @Column(name = "image_url")
    private String imageUrl;

    // Many-to-many: a component can use several materials and a material can be used by many components.
    // The links are stored in the component_materials join table: joinColumns is the column that points
    // to this component, inverseJoinColumns the column that points to the material.
    // @ManyToMany is lazy by default. @OrderBy("name") returns the materials sorted A to Z.
    @ManyToMany
    @JoinTable(name = "component_materials",
            joinColumns = @JoinColumn(name = "component_id"),
            inverseJoinColumns = @JoinColumn(name = "material_id"))
    @OrderBy("name")
    private List<Material> materials = new ArrayList<>();

    // protected no-argument constructor: needed by JPA to create objects from rows
    protected Component() {
        // Required by JPA
    }

    public Component(ComponentSystem system, String name, String description,
                     String purpose, String operation) {
        this.system = system;
        this.name = name;
        this.description = description;
        this.purpose = purpose;
        this.operation = operation;
    }

    // Getters used by the templates, e.g. ${component.name} calls getName()
    public Integer getId() {
        return id;
    }

    public ComponentSystem getSystem() {
        return system;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getOperation() {
        return operation;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<Material> getMaterials() {
        return materials;
    }
}
