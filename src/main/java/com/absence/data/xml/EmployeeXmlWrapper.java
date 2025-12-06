package com.absence.data.xml;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * JAXB wrapper class for XML serialization of the employee collection.
 *
 * Architecture: Data Layer - This wrapper exists solely to facilitate
 * JAXB serialization of the root employees element. It has no business
 * logic and is purely a technical necessity for XML handling.
 */
@XmlRootElement(name = "employees")
@XmlAccessorType(XmlAccessType.FIELD)
public class EmployeeXmlWrapper {

    @XmlElement(name = "employee")
    private List<EmployeeXmlModel> employees = new ArrayList<>();

    public EmployeeXmlWrapper() {
    }

    public EmployeeXmlWrapper(List<EmployeeXmlModel> employees) {
        this.employees = employees;
    }

    public List<EmployeeXmlModel> getEmployees() {
        return employees;
    }

    public void setEmployees(List<EmployeeXmlModel> employees) {
        this.employees = employees;
    }
}
