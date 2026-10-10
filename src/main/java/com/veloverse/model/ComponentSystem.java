package com.veloverse.model;

import java.util.ArrayList;
import java.util.List;

// JPA annotations that describe how this class and its relationships map to database tables
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

/**
 * A group of related components, such as the Drivetrain or Braking System.
 * Maps to the component_systems table.
 */
// @Entity: a JPA entity, so each object of this class is one row of the table named in @Table.
@Entity
@Table(name = "component_systems")
public class ComponentSystem {

    // Primary key, numbered automatically by PostgreSQL (IDENTITY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "system_id")
    private Integer id;

    private String name;

    private String description;

    // One system has many components. mappedBy = "system" means the foreign key is owned by the
    // system field in Component, so this side adds no extra column. @OrderBy("id") keeps them in ID order.
    // @OneToMany is lazy by default; ComponentSystemRepository loads the components with @EntityGraph.
    @OneToMany(mappedBy = "system")
    @OrderBy("id")
    private List<Component> components = new ArrayList<>();

    // protected no-argument constructor: needed by JPA to create objects from rows
    protected ComponentSystem() {
        // Required by JPA
    }

    public ComponentSystem(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters used by the templates, e.g. ${system.name} calls getName()
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Component> getComponents() {
        return components;
    }
}
