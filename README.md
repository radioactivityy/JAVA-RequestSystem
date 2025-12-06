# Employee Absence Request System

A Java 17+ enterprise application demonstrating three-tier architecture, Repository Pattern, Service Layer, DTO Pattern, and Singleton Pattern.

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                          │
│  ┌─────────────────────┐    ┌─────────────────────┐            │
│  │ ManagerCommandLine  │    │ EmployeeWebController│            │
│  │    Interface (CLI)  │    │     (Servlet)       │            │
│  └──────────┬──────────┘    └──────────┬──────────┘            │
└─────────────┼──────────────────────────┼────────────────────────┘
              │                          │
              │     Uses DTOs            │
              ▼                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                       DOMAIN LAYER                              │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                   AbsenceService                         │   │
│  │         (Business Logic - Storage Agnostic)              │   │
│  └───────────────────────┬─────────────────────────────────┘   │
│                          │                                      │
│  ┌─────────────┐  ┌─────────────────┐  ┌───────────────────┐   │
│  │   Models    │  │  Repository     │  │      DTOs         │   │
│  │ - Employee  │  │  Interfaces     │  │ - EmployeeDTO     │   │
│  │ - Manager   │  │ - EmployeeRepo  │  │ - AbsenceReqDTO   │   │
│  │ - Request   │  │ - ManagerRepo   │  │ - ManagerDTO      │   │
│  └─────────────┘  └────────┬────────┘  └───────────────────┘   │
└────────────────────────────┼────────────────────────────────────┘
                             │
                             │ Implements
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                        DATA LAYER                               │
│  ┌─────────────────────┐    ┌─────────────────────┐            │
│  │ XmlEmployeeRepository│    │ JsonManagerRepository│            │
│  │    (JAXB)           │    │    (Jackson)        │            │
│  └──────────┬──────────┘    └──────────┬──────────┘            │
│             │                          │                        │
│             ▼                          ▼                        │
│     employees.xml               managers.json                   │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │          RepositoryRegistry (Singleton)                  │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

## Design Patterns Implemented

### 1. Repository Pattern
Abstracts data access, allowing different storage mechanisms (XML/JSON) while providing a uniform interface.

### 2. Service Layer
`AbsenceService` encapsulates business logic, remaining completely agnostic of storage formats.

### 3. DTO Pattern
Data Transfer Objects decouple presentation from domain internals.

### 4. Singleton Pattern
`RepositoryRegistry` provides centralized access to repositories and services.

## Dependency Rule

- **Domain Layer**: Has no external dependencies (pure POJOs)
- **Presentation Layer**: Depends on Domain (via AbsenceService and DTOs)
- **Data Layer**: Depends on Domain (implements Repository interfaces)
- **Presentation NEVER depends on Data directly**

## Project Structure

```
src/main/java/com/absence/
├── domain/
│   ├── model/           # POJOs: Employee, Manager, AbsenceRequest, RequestStatus
│   ├── repository/      # Interfaces: EmployeeRepository, ManagerRepository
│   ├── service/         # AbsenceService interface and implementation
│   └── dto/             # DTOs for data transfer
├── data/
│   ├── xml/             # JAXB-based XML repository for Employees
│   ├── json/            # Jackson-based JSON repository for Managers
│   └── registry/        # Singleton RepositoryRegistry
├── presentation/
│   ├── cli/             # Manager Command Line Interface
│   └── web/             # Employee Servlet Controller
└── Application.java     # Entry point

data/
├── employees.xml        # Employee and AbsenceRequest storage
└── managers.json        # Manager storage
```

## Building and Running

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Build
```bash
mvn clean package
```

### Run Manager CLI
```bash
java -jar target/employee-absence-system-1.0.0.jar
```

### Run Demo Mode
```bash
java -jar target/employee-absence-system-1.0.0.jar --demo
```

### Sample Manager IDs for Testing
- `MGR001` - Frank Miller (Engineering)
- `MGR002` - Grace Lee (Marketing)
- `MGR003` - Henry Wilson (All Departments)

## Storage Strategy

| Entity | Format | File |
|--------|--------|------|
| Employees & Requests | XML (JAXB) | data/employees.xml |
| Managers | JSON (Jackson) | data/managers.json |

The Service layer is completely unaware of these storage details, demonstrating proper abstraction.
