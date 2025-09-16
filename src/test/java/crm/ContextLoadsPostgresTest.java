package com.alemandan.crm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = AlemandanSoftwareApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("postgres")
class ContextLoadsPostgresTests {

    @Test
    void contextLoads() {
        // Smoke test: levanta el contexto, aplica Flyway y conecta a Postgres.
    }
}