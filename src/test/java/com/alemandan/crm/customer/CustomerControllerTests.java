package com.alemandan.crm.customer;

import com.alemandan.crm.AlemandanSoftwareApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest(
        classes = AlemandanSoftwareApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@AutoConfigureMockMvc
class CustomerControllerTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @Autowired CustomerRepository repo;

    @BeforeEach
    void clean() { repo.deleteAll(); }

    @Test
    @WithMockUser
    void createAndGetCustomer() throws Exception {
        var body = """
                {
                  "name": "Ada Lovelace",
                  "email": "ada@example.com",
                  "phone": "+1 555 0100"
                }
                """;

        String location = mvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn().getResponse().getHeader("Location");

        mvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ada Lovelace"))
                .andExpect(jsonPath("$.email").value("ada@example.com"));

        mvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("ada@example.com"));
    }

    @Test
    @WithMockUser
    void testPaginatedCustomers() throws Exception {
        // Create multiple customers for pagination testing
        var customer1 = """
                {
                  "name": "Alice Smith",
                  "email": "alice@example.com",
                  "phone": "+1 555 0101"
                }
                """;
        var customer2 = """
                {
                  "name": "Bob Johnson",
                  "email": "bob@example.com",
                  "phone": "+1 555 0102"
                }
                """;
        var customer3 = """
                {
                  "name": "Charlie Brown",
                  "email": "charlie@example.com",
                  "phone": "+1 555 0103"
                }
                """;

        // Create customers
        mvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customer1)
                        .with(csrf()))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customer2)
                        .with(csrf()))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customer3)
                        .with(csrf()))
                .andExpect(status().isCreated());

        // Test pagination with page=0, size=2
        mvc.perform(get("/api/customers/page")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false));

        // Test pagination with page=1, size=2
        mvc.perform(get("/api/customers/page")
                        .param("page", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.pageable.pageNumber").value(1))
                .andExpect(jsonPath("$.pageable.pageSize").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.first").value(false))
                .andExpect(jsonPath("$.last").value(true));

        // Test sorting by name ascending
        mvc.perform(get("/api/customers/page")
                        .param("page", "0")
                        .param("size", "3")
                        .param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Alice Smith"))
                .andExpect(jsonPath("$.content[1].name").value("Bob Johnson"))
                .andExpect(jsonPath("$.content[2].name").value("Charlie Brown"));

        // Test sorting by name descending
        mvc.perform(get("/api/customers/page")
                        .param("page", "0")
                        .param("size", "3")
                        .param("sort", "name,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Charlie Brown"))
                .andExpect(jsonPath("$.content[1].name").value("Bob Johnson"))
                .andExpect(jsonPath("$.content[2].name").value("Alice Smith"));
    }
}