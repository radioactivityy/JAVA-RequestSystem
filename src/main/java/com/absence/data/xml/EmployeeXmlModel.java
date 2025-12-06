package com.absence.data.xml;

import com.absence.domain.model.Employee;
import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * XML model for Employee serialization.
 *
 * Architecture: Data Layer - JAXB-annotated model that provides XML
 * serialization for Employee entities. This class serves as an
 * Anti-Corruption Layer, preventing JAXB annotations from polluting
 * the domain model.
 *
 * Design Decision: Separating XML models from domain models allows:
 * - Domain entities to remain framework-agnostic (pure POJOs)
 * - XML schema changes without affecting business logic
 * - Different serialization strategies per storage format
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class EmployeeXmlModel {

    @XmlAttribute
    private String id;

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "email")
    private String email;

    @XmlElement(name = "department")
    private String department;

    @XmlElement(name = "annualLeaveBalance")
    private int annualLeaveBalance;

    @XmlElementWrapper(name = "absenceRequests")
    @XmlElement(name = "request")
    private List<AbsenceRequestXmlModel> absenceRequests = new ArrayList<>();

    public EmployeeXmlModel() {
    }

    public static EmployeeXmlModel fromDomain(Employee employee) {
        EmployeeXmlModel model = new EmployeeXmlModel();
        model.id = employee.getId();
        model.name = employee.getName();
        model.email = employee.getEmail();
        model.department = employee.getDepartment();
        model.annualLeaveBalance = employee.getAnnualLeaveBalance();
        model.absenceRequests = employee.getAbsenceRequests().stream()
                .map(AbsenceRequestXmlModel::fromDomain)
                .toList();
        return model;
    }

    public Employee toDomain() {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setName(name);
        employee.setEmail(email);
        employee.setDepartment(department);
        employee.setAnnualLeaveBalance(annualLeaveBalance);
        employee.setAbsenceRequests(
                absenceRequests.stream()
                        .map(AbsenceRequestXmlModel::toDomain)
                        .toList()
        );
        return employee;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getAnnualLeaveBalance() {
        return annualLeaveBalance;
    }

    public void setAnnualLeaveBalance(int annualLeaveBalance) {
        this.annualLeaveBalance = annualLeaveBalance;
    }

    public List<AbsenceRequestXmlModel> getAbsenceRequests() {
        return absenceRequests;
    }

    public void setAbsenceRequests(List<AbsenceRequestXmlModel> absenceRequests) {
        this.absenceRequests = absenceRequests;
    }
}
