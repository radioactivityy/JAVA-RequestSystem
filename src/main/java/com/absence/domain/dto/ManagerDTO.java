package com.absence.domain.dto;

import com.absence.domain.model.Manager;

/**
 * Data Transfer Object for Manager entities.
 *
 * Architecture: Domain Layer - Provides a view of manager data for
 * the Presentation layer without exposing internal details.
 *
 * Design Pattern: DTO Pattern - Facilitates clean separation between
 * presentation and domain layers.
 */
public record ManagerDTO(
        String id,
        String name,
        String email,
        String department,
        boolean active
) {

    /**
     * Factory method to create a DTO from a domain entity.
     */
    public static ManagerDTO fromEntity(Manager manager) {
        return new ManagerDTO(
                manager.getId(),
                manager.getName(),
                manager.getEmail(),
                manager.getDepartment(),
                manager.isActive()
        );
    }

    /**
     * Returns a formatted string representation for display.
     */
    public String toDisplayString() {
        return String.format(
                "Manager: %s (ID: %s)%n" +
                "Email: %s%n" +
                "Department: %s%n" +
                "Status: %s",
                name, id, email, department,
                active ? "Active" : "Inactive"
        );
    }
}
