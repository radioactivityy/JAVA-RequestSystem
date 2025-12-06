package com.absence.data.xml;

import com.absence.domain.model.AbsenceRequest;
import com.absence.domain.model.Employee;
import com.absence.domain.model.RequestStatus;
import com.absence.domain.repository.EmployeeRepository;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * XML-based implementation of the EmployeeRepository interface.
 *
 * Architecture: Data Layer - Concrete implementation of the repository
 * interface using JAXB for XML persistence. This class depends on the
 * Domain layer (repository interface) but the Domain layer has no
 * knowledge of this implementation.
 *
 * Design Pattern: Repository Pattern - Encapsulates all XML-specific
 * data access logic, providing a clean abstraction to the Service layer.
 *
 * Thread Safety: Uses ReadWriteLock for concurrent access support.
 */
public class XmlEmployeeRepository implements EmployeeRepository {

    private final String filePath;
    private final JAXBContext jaxbContext;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private List<Employee> cachedEmployees;

    public XmlEmployeeRepository(String filePath) {
        this.filePath = filePath;
        try {
            this.jaxbContext = JAXBContext.newInstance(EmployeeXmlWrapper.class);
            loadEmployees();
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to initialize JAXB context", e);
        }
    }

    private void loadEmployees() {
        lock.writeLock().lock();
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                cachedEmployees = new ArrayList<>();
                return;
            }

            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            try (FileReader reader = new FileReader(file)) {
                EmployeeXmlWrapper wrapper = (EmployeeXmlWrapper) unmarshaller.unmarshal(reader);
                cachedEmployees = wrapper.getEmployees().stream()
                        .map(EmployeeXmlModel::toDomain)
                        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            }
        } catch (JAXBException | IOException e) {
            throw new RuntimeException("Failed to load employees from XML", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void saveEmployees() {
        lock.writeLock().lock();
        try {
            List<EmployeeXmlModel> xmlModels = cachedEmployees.stream()
                    .map(EmployeeXmlModel::fromDomain)
                    .toList();

            EmployeeXmlWrapper wrapper = new EmployeeXmlWrapper(new ArrayList<>(xmlModels));

            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            File file = new File(filePath);
            file.getParentFile().mkdirs();

            try (FileWriter writer = new FileWriter(file)) {
                marshaller.marshal(wrapper, writer);
            }
        } catch (JAXBException | IOException e) {
            throw new RuntimeException("Failed to save employees to XML", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Optional<Employee> findById(String id) {
        lock.readLock().lock();
        try {
            return cachedEmployees.stream()
                    .filter(e -> e.getId().equals(id))
                    .findFirst();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Optional<Employee> findByEmail(String email) {
        lock.readLock().lock();
        try {
            return cachedEmployees.stream()
                    .filter(e -> e.getEmail().equalsIgnoreCase(email))
                    .findFirst();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<Employee> findAll() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(cachedEmployees);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<Employee> findByDepartment(String department) {
        lock.readLock().lock();
        try {
            return cachedEmployees.stream()
                    .filter(e -> e.getDepartment().equalsIgnoreCase(department))
                    .toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Employee save(Employee employee) {
        lock.writeLock().lock();
        try {
            Optional<Employee> existing = cachedEmployees.stream()
                    .filter(e -> e.getId().equals(employee.getId()))
                    .findFirst();

            if (existing.isPresent()) {
                int index = cachedEmployees.indexOf(existing.get());
                cachedEmployees.set(index, employee);
            } else {
                cachedEmployees.add(employee);
            }

            saveEmployees();
            return employee;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean deleteById(String id) {
        lock.writeLock().lock();
        try {
            boolean removed = cachedEmployees.removeIf(e -> e.getId().equals(id));
            if (removed) {
                saveEmployees();
            }
            return removed;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Optional<AbsenceRequest> findAbsenceRequestById(String requestId) {
        lock.readLock().lock();
        try {
            return cachedEmployees.stream()
                    .flatMap(e -> e.getAbsenceRequests().stream())
                    .filter(r -> r.getId().equals(requestId))
                    .findFirst();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<AbsenceRequest> findAbsenceRequestsByStatus(RequestStatus status) {
        lock.readLock().lock();
        try {
            return cachedEmployees.stream()
                    .flatMap(e -> e.getAbsenceRequests().stream())
                    .filter(r -> r.getStatus() == status)
                    .toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean updateAbsenceRequest(AbsenceRequest request) {
        lock.writeLock().lock();
        try {
            for (Employee employee : cachedEmployees) {
                List<AbsenceRequest> requests = new ArrayList<>(employee.getAbsenceRequests());
                for (int i = 0; i < requests.size(); i++) {
                    if (requests.get(i).getId().equals(request.getId())) {
                        requests.set(i, request);
                        employee.setAbsenceRequests(requests);
                        saveEmployees();
                        return true;
                    }
                }
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }
}
