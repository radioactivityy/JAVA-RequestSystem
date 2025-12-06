package com.absence.domain.model;

/**
 * Enumeration representing the possible states of an absence request.
 *
 * Architecture: Domain Layer - Core business concept that defines the
 * lifecycle states of an absence request. This enum has no external
 * dependencies, adhering to the Dependency Rule.
 */
public enum RequestStatus {

    PENDING("Pending Review"),
    APPROVED("Approved"),
    REJECTED("Rejected");

    private final String displayName;

    RequestStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
