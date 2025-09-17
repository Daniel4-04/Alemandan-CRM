package com.alemandan.crm.customer;

import com.alemandan.crm.common.ResourceNotFoundException;
import com.alemandan.crm.customer.dto.CustomerCreateRequest;
import com.alemandan.crm.customer.dto.CustomerResponse;
import com.alemandan.crm.customer.dto.CustomerUpdateRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            throw e; // Lo traduce el RestExceptionHandler a 409
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

    @Transactional(readOnly = true)
    public Page<CustomerResponse> listPage(Pageable pageable) {
        return repository.findAll(pageable).map(this::toResponse);
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