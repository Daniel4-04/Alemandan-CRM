package com.alemandan.crm.customer;

import com.alemandan.crm.common.ApiError;
import com.alemandan.crm.customer.dto.CustomerCreateRequest;
import com.alemandan.crm.customer.dto.CustomerResponse;
import com.alemandan.crm.customer.dto.CustomerUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(path = "/api/customers", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(summary = "Listar clientes (lista completa)")
    @ApiResponse(responseCode = "200", description = "Listado de clientes")
    @GetMapping
    public ResponseEntity<List<CustomerResponse>> list() {
        List<CustomerResponse> items = customerService.list();
        return ResponseEntity.ok(items);
    }

    @Operation(summary = "Listar clientes paginados")
    @ApiResponse(responseCode = "200", description = "Página de clientes")
    @GetMapping("/page")
    public ResponseEntity<Page<CustomerResponse>> listPage(@ParameterObject Pageable pageable) {
        Page<CustomerResponse> page = customerService.listPage(pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Obtener cliente por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "No encontrado", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> get(@PathVariable Long id) {
        CustomerResponse dto = customerService.get(id);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Crear cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Creado", content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto (email duplicado)", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerResponse> create(@RequestBody @Validated CustomerCreateRequest request) {
        CustomerResponse created = customerService.create(request);
        URI location = URI.create("/api/customers/" + created.getId());
        return ResponseEntity.created(location).body(created); // 201 Created + Location
    }

    @Operation(summary = "Actualizar cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Actualizado"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "No encontrado", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto (email duplicado)", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerResponse> update(@PathVariable Long id,
                                                   @RequestBody @Validated CustomerUpdateRequest request) {
        CustomerResponse updated = customerService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Eliminar cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Eliminado"),
            @ApiResponse(responseCode = "404", description = "No encontrado", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}