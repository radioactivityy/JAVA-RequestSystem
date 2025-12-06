package com.absence.presentation.web;

import com.absence.domain.dto.AbsenceRequestDTO;
import com.absence.domain.dto.EmployeeDTO;
import com.absence.domain.service.AbsenceService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Servlet-based web controller for employee absence request operations.
 *
 * Architecture: Presentation Layer - Handles HTTP requests for the employee
 * context. Communicates with the Domain layer via AbsenceService and DTOs.
 *
 * Dependency Rule: This controller depends only on the Domain layer through
 * the AbsenceService interface and DTOs. It has NO direct dependency on the
 * Data layer (repositories, XML/JSON implementations).
 *
 * Endpoints:
 * - GET  /absence/employee/{id}           - Get employee info and requests
 * - GET  /absence/employee/{id}/requests  - List all requests for employee
 * - POST /absence/employee/{id}/request   - Submit new absence request
 * - DELETE /absence/employee/{id}/request/{requestId} - Cancel pending request
 *
 * Note: This is a simplified servlet implementation. In production, consider
 * using JAX-RS, Spring MVC, or another web framework.
 */
@WebServlet(urlPatterns = "/absence/employee/*")
public class EmployeeWebController extends HttpServlet {

    private AbsenceService absenceService;

    public EmployeeWebController() {
    }

    public EmployeeWebController(AbsenceService absenceService) {
        this.absenceService = absenceService;
    }

    /**
     * Allows injection of AbsenceService for testing or manual configuration.
     */
    public void setAbsenceService(AbsenceService absenceService) {
        this.absenceService = absenceService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Employee ID is required");
            return;
        }

        String[] pathParts = pathInfo.split("/");
        if (pathParts.length < 2) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid path format");
            return;
        }

        String employeeId = pathParts[1];

        if (pathParts.length >= 3 && "requests".equals(pathParts[2])) {
            handleGetRequests(employeeId, response);
        } else {
            handleGetEmployee(employeeId, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (pathInfo == null) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Employee ID is required");
            return;
        }

        String[] pathParts = pathInfo.split("/");
        if (pathParts.length < 3 || !"request".equals(pathParts[2])) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid endpoint. Use /absence/employee/{id}/request");
            return;
        }

        String employeeId = pathParts[1];
        handleSubmitRequest(employeeId, request, response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (pathInfo == null) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid path");
            return;
        }

        String[] pathParts = pathInfo.split("/");
        if (pathParts.length < 4 || !"request".equals(pathParts[2])) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid endpoint. Use /absence/employee/{id}/request/{requestId}");
            return;
        }

        String employeeId = pathParts[1];
        String requestId = pathParts[3];
        handleCancelRequest(employeeId, requestId, response);
    }

    private void handleGetEmployee(String employeeId, HttpServletResponse response)
            throws IOException {

        var employeeOpt = absenceService.getEmployee(employeeId);
        if (employeeOpt.isPresent()) {
            sendJsonResponse(response, HttpServletResponse.SC_OK,
                    formatEmployeeJson(employeeOpt.get()));
        } else {
            sendError(response, HttpServletResponse.SC_NOT_FOUND,
                    "Employee not found: " + employeeId);
        }
    }

    private void handleGetRequests(String employeeId, HttpServletResponse response)
            throws IOException {

        List<AbsenceRequestDTO> requests = absenceService.getEmployeeRequests(employeeId);
        sendJsonResponse(response, HttpServletResponse.SC_OK, formatRequestListJson(requests));
    }

    private void handleSubmitRequest(String employeeId, HttpServletRequest request,
                                      HttpServletResponse response) throws IOException {

        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        String reason = request.getParameter("reason");

        if (startDateStr == null || endDateStr == null) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "startDate and endDate are required (format: YYYY-MM-DD)");
            return;
        }

        try {
            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = LocalDate.parse(endDateStr);

            AbsenceRequestDTO result = absenceService.submitRequest(
                    employeeId, startDate, endDate, reason);

            sendJsonResponse(response, HttpServletResponse.SC_CREATED,
                    formatRequestJson(result));

        } catch (DateTimeParseException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid date format. Use YYYY-MM-DD");
        } catch (IllegalArgumentException | IllegalStateException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    private void handleCancelRequest(String employeeId, String requestId,
                                      HttpServletResponse response) throws IOException {

        try {
            boolean cancelled = absenceService.cancelRequest(employeeId, requestId);
            if (cancelled) {
                sendJsonResponse(response, HttpServletResponse.SC_OK,
                        "{\"message\": \"Request cancelled successfully\"}");
            } else {
                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Failed to cancel request");
            }
        } catch (IllegalStateException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    private void sendJsonResponse(HttpServletResponse response, int status, String json)
            throws IOException {
        response.setStatus(status);
        try (PrintWriter writer = response.getWriter()) {
            writer.write(json);
        }
    }

    private void sendError(HttpServletResponse response, int status, String message)
            throws IOException {
        response.setStatus(status);
        try (PrintWriter writer = response.getWriter()) {
            writer.write(String.format("{\"error\": \"%s\"}", escapeJson(message)));
        }
    }

    private String formatEmployeeJson(EmployeeDTO employee) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(String.format("  \"id\": \"%s\",\n", escapeJson(employee.id())));
        sb.append(String.format("  \"name\": \"%s\",\n", escapeJson(employee.name())));
        sb.append(String.format("  \"email\": \"%s\",\n", escapeJson(employee.email())));
        sb.append(String.format("  \"department\": \"%s\",\n", escapeJson(employee.department())));
        sb.append(String.format("  \"annualLeaveBalance\": %d,\n", employee.annualLeaveBalance()));
        sb.append(String.format("  \"remainingLeaveBalance\": %d,\n", employee.remainingLeaveBalance()));
        sb.append(String.format("  \"pendingRequestsCount\": %d\n", employee.pendingRequestsCount()));
        sb.append("}");
        return sb.toString();
    }

    private String formatRequestJson(AbsenceRequestDTO request) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(String.format("  \"id\": \"%s\",\n", escapeJson(request.id())));
        sb.append(String.format("  \"employeeId\": \"%s\",\n", escapeJson(request.employeeId())));
        sb.append(String.format("  \"employeeName\": \"%s\",\n", escapeJson(request.employeeName())));
        sb.append(String.format("  \"startDate\": \"%s\",\n", request.startDate()));
        sb.append(String.format("  \"endDate\": \"%s\",\n", request.endDate()));
        sb.append(String.format("  \"durationDays\": %d,\n", request.durationDays()));
        sb.append(String.format("  \"reason\": \"%s\",\n", escapeJson(request.reason())));
        sb.append(String.format("  \"status\": \"%s\",\n", escapeJson(request.status())));
        sb.append(String.format("  \"submissionDate\": \"%s\"\n", request.submissionDate()));
        sb.append("}");
        return sb.toString();
    }

    private String formatRequestListJson(List<AbsenceRequestDTO> requests) {
        if (requests.isEmpty()) {
            return "{\"requests\": []}";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{\"requests\": [\n");

        for (int i = 0; i < requests.size(); i++) {
            sb.append("  ").append(formatRequestJson(requests.get(i)).replace("\n", "\n  "));
            if (i < requests.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }

        sb.append("]}");
        return sb.toString();
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
