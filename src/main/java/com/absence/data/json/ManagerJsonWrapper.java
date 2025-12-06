package com.absence.data.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON wrapper class for the managers collection.
 *
 * Architecture: Data Layer - Facilitates JSON serialization of the
 * root managers object. Purely a technical necessity for Jackson.
 */
public class ManagerJsonWrapper {

    @JsonProperty("managers")
    private List<ManagerJsonModel> managers = new ArrayList<>();

    public ManagerJsonWrapper() {
    }

    public ManagerJsonWrapper(List<ManagerJsonModel> managers) {
        this.managers = managers;
    }

    public List<ManagerJsonModel> getManagers() {
        return managers;
    }

    public void setManagers(List<ManagerJsonModel> managers) {
        this.managers = managers;
    }
}
