package com.alemandan.crm.customer;

import com.alemandan.crm.common.ResourceNotFoundException;
import com.alemandan.crm.customer.dto.CustomerCreateRequest;
import com.alemandan.crm.customer.dto.CustomerResponse;
import com.alemandan.crm.customer.dto.CustomerUpdateRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    public CustomerResponse create(CustomerCreateRequest req) {
        Customer c = new Customer();
        c.setName(req.getName());
        c.setEmail(req.getEmail());
        c.setPhone(req.getPhone());
        try {
            Customer saved = repository.save(c);
            return toResponse(saved);
        } catch (DataIntegrityViolationException e) {
            // Deja que el ControllerAdvice lo traduzca a 409
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public CustomerResponse get(Long id) {
        Customer c = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer " + id + " not found"));
        return toResponse(c);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> list() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public CustomerResponse update(Long id, CustomerUpdateRequest req) {
        Customer c = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer " + id + " not found"));
        c.setName(req.getName());
        c.setEmail(req.getEmail());
        c.setPhone(req.getPhone());
        Customer saved = repository.save(c);
        return toResponse(saved);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Customer " + id + " not found");
        }
        repository.deleteById(id);
    }

    private CustomerResponse toResponse(Customer c) {
        return new CustomerResponse(
                c.getId(), c.getName(), c.getEmail(), c.getPhone(),
                c.getCreatedAt(), c.getUpdatedAt()
        );
    }
}