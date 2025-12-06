package com.absence.domain.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing an employee's request for absence/leave.
 *
 * Architecture: Domain Layer - Pure POJO with no framework dependencies.
 * Contains business logic for calculating duration and validating dates.
 * The Domain layer depends on nothing external, satisfying the Dependency Rule.
 */
public class AbsenceRequest {

    private String id;
    private String employeeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private RequestStatus status;
    private String processedByManagerId;
    private LocalDate submissionDate;

    public AbsenceRequest() {
        this.id = UUID.randomUUID().toString();
        this.status = RequestStatus.PENDING;
        this.submissionDate = LocalDate.now();
    }

    public AbsenceRequest(String employeeId, LocalDate startDate, LocalDate endDate, String reason) {
        this();
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
    }

    /**
     * Calculates the number of days requested for absence.
     * Business logic encapsulated within the domain entity.
     */
    public long calculateDuration() {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return startDate.until(endDate).getDays() + 1;
    }

    /**
     * Validates that the request dates are logically correct.
     */
    public boolean isValid() {
        if (startDate == null || endDate == null) {
            return false;
        }
        return !endDate.isBefore(startDate) && !startDate.isBefore(LocalDate.now());
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public String getProcessedByManagerId() {
        return processedByManagerId;
    }

    public void setProcessedByManagerId(String processedByManagerId) {
        this.processedByManagerId = processedByManagerId;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbsenceRequest that = (AbsenceRequest) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AbsenceRequest{" +
                "id='" + id + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                '}';
    }
}
