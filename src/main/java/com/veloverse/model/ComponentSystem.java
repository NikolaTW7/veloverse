package com.veloverse.model;

import java.util.ArrayList;
import java.util.List;

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
@Entity
@Table(name = "component_systems")
public class ComponentSystem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "system_id")
    private Integer id;

    private String name;

    private String description;

    @OneToMany(mappedBy = "system")
    @OrderBy("id")
    private List<Component> components = new ArrayList<>();

    protected ComponentSystem() {
        // Required by JPA
    }

    public ComponentSystem(String name, String description) {
        this.name = name;
        this.description = description;
    }

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
