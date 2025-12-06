package com.absence.data.xml;

import com.absence.domain.model.AbsenceRequest;
import com.absence.domain.model.RequestStatus;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.time.LocalDate;

/**
 * XML model for AbsenceRequest serialization.
 *
 * Architecture: Data Layer - JAXB-annotated model that mirrors the domain
 * AbsenceRequest entity. Provides bidirectional conversion between domain
 * entities and XML representations.
 *
 * This class isolates JAXB annotations from the domain model, keeping the
 * Domain layer free of persistence framework dependencies.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class AbsenceRequestXmlModel {

    @XmlAttribute
    private String id;

    @XmlElement(name = "employeeId")
    private String employeeId;

    @XmlElement(name = "startDate")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate startDate;

    @XmlElement(name = "endDate")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate endDate;

    @XmlElement(name = "reason")
    private String reason;

    @XmlElement(name = "status")
    private String status;

    @XmlElement(name = "processedByManagerId")
    private String processedByManagerId;

    @XmlElement(name = "submissionDate")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate submissionDate;

    public AbsenceRequestXmlModel() {
    }

    public static AbsenceRequestXmlModel fromDomain(AbsenceRequest request) {
        AbsenceRequestXmlModel model = new AbsenceRequestXmlModel();
        model.id = request.getId();
        model.employeeId = request.getEmployeeId();
        model.startDate = request.getStartDate();
        model.endDate = request.getEndDate();
        model.reason = request.getReason();
        model.status = request.getStatus().name();
        model.processedByManagerId = request.getProcessedByManagerId();
        model.submissionDate = request.getSubmissionDate();
        return model;
    }

    public AbsenceRequest toDomain() {
        AbsenceRequest request = new AbsenceRequest();
        request.setId(id);
        request.setEmployeeId(employeeId);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setReason(reason);
        request.setStatus(RequestStatus.valueOf(status));
        request.setProcessedByManagerId(processedByManagerId);
        request.setSubmissionDate(submissionDate);
        return request;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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
}
