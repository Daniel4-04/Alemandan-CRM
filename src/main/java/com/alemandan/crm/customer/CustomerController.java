package com.alemandan.crm.customer;

import com.alemandan.crm.customer.dto.CustomerCreateRequest;
import com.alemandan.crm.customer.dto.CustomerResponse;
import com.alemandan.crm.customer.dto.CustomerUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerCreateRequest req) {
        CustomerResponse created = service.create(req);
        return ResponseEntity
                .created(URI.create("/api/customers/" + created.getId()))
                .body(created);
    }

    @GetMapping("/{id}")
    public CustomerResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping
    public List<CustomerResponse> list() {
        return service.list();
    }

    @PutMapping("/{id}")
    public CustomerResponse update(@PathVariable Long id, @Valid @RequestBody CustomerUpdateRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}