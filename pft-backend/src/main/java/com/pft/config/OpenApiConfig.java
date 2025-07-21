package com.pft.config;

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

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Personal Finance Tracker API")
                        .description("""
                                A comprehensive REST API for personal finance management.
                                
                                ## Features
                                - **User Management**: Registration, authentication, and profile management
                                - **Account Management**: Multiple account types with balance tracking
                                - **Transaction Management**: Income, expenses, and transfers
                                - **Category Management**: Customizable transaction categories
                                - **Budget Management**: Spending limits and alerts
                                - **Goal Management**: Financial goals with progress tracking
                                
                                ## Authentication
                                This API uses JWT (JSON Web Token) authentication. Include the token in the Authorization header:
                                ```
                                Authorization: Bearer <your-jwt-token>
                                ```
                                
                                ## Getting Started
                                1. Register a new user using `/auth/register`
                                2. Login using `/auth/login` to get your JWT token
                                3. Use the token in subsequent API calls
                                
                                ## Rate Limiting
                                - 100 requests per minute per user
                                - 1000 requests per hour per user
                                
                                ## Error Handling
                                The API returns standard HTTP status codes:
                                - `200`: Success
                                - `201`: Created
                                - `400`: Bad Request
                                - `401`: Unauthorized
                                - `403`: Forbidden
                                - `404`: Not Found
                                - `500`: Internal Server Error
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Personal Finance Tracker Team")
                                .email("support@pft.com")
                                .url("https://github.com/pft/personal-finance-tracker"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Development Server"),
                        new Server().url("https://api.pft.com").description("Production Server")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token for authentication")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
} 