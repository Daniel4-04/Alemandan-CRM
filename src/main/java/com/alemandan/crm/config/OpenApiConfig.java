package com.alemandan.crm.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI alemandanOpenAPI() {
        SecurityScheme basicAuthScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("basic");

        return new OpenAPI()
                .info(new Info()
                        .title("Alemandan CRM API")
                        .description("API para gestión de clientes (Alemandan Software)")
                        .version("v1")
                        .contact(new Contact()
                                .name("Soporte Alemandan")
                                .email("soporte@alemandan.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Desarrollo"))
                .components(new Components()
                        .addSecuritySchemes("basicAuth", basicAuthScheme))
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"));
    }
}