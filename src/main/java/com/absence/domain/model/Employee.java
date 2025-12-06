package com.absence.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Domain entity representing an employee who can submit absence requests.
 *
 * Architecture: Domain Layer - Pure POJO containing employee data and
 * their associated absence requests. Maintains the Aggregate Root pattern
 * where Employee owns its AbsenceRequests. No external dependencies.
 */
public class Employee {

    private String id;
    private String name;
    private String email;
    private String department;
    private int annualLeaveBalance;
    private List<AbsenceRequest> absenceRequests;

    public Employee() {
        this.absenceRequests = new ArrayList<>();
    }

    public Employee(String id, String name, String email, String department, int annualLeaveBalance) {
        this();
        this.id = id;
        this.name = name;
        this.email = email;
        this.department = department;
        this.annualLeaveBalance = annualLeaveBalance;
    }

    /**
     * Adds a new absence request to this employee.
     * Business logic: automatically sets the employee ID on the request.
     */
    public void addAbsenceRequest(AbsenceRequest request) {
        request.setEmployeeId(this.id);
        this.absenceRequests.add(request);
    }

    /**
     * Calculates remaining leave balance after accounting for approved requests.
     */
    public int calculateRemainingLeave() {
        long usedDays = absenceRequests.stream()
                .filter(r -> r.getStatus() == RequestStatus.APPROVED)
                .mapToLong(AbsenceRequest::calculateDuration)
                .sum();
        return (int) (annualLeaveBalance - usedDays);
    }

    /**
     * Checks if the employee has sufficient leave balance for a request.
     */
    public boolean hasSufficientBalance(AbsenceRequest request) {
        return calculateRemainingLeave() >= request.calculateDuration();
    }

    /**
     * Returns an unmodifiable view of absence requests.
     */
    public List<AbsenceRequest> getAbsenceRequests() {
        return Collections.unmodifiableList(absenceRequests);
    }

    public void setAbsenceRequests(List<AbsenceRequest> absenceRequests) {
        this.absenceRequests = new ArrayList<>(absenceRequests);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(id, employee.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", remainingLeave=" + calculateRemainingLeave() +
                '}';
    }
}
