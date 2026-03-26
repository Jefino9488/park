package com.hcl.parkingslot.parking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String BEARER_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI parkingLotOpenApi() {
        return new OpenAPI()
            .components(new Components().addSecuritySchemes(BEARER_SCHEME_NAME,
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Paste JWT token from /api/auth/login")))
                .info(new Info()
                        .title("Parking Slot Management API")
                        .description("Backend APIs for parking slot tracking, allocation, session billing, and authentication")
                        .version("v1")
                        .contact(new Contact().name("Parking Ops Team").email("support@parkingslot.local"))
                        .license(new License().name("Internal Use")));
    }

    @Bean
    public GroupedOpenApi authApiGroup() {
        return GroupedOpenApi.builder()
                .group("01-auth")
                .pathsToMatch("/api/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi parkingApiGroup() {
        return GroupedOpenApi.builder()
                .group("02-parking")
                .pathsToMatch("/api/parking/**")
                .addOpenApiCustomizer(openApi -> openApi.addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME_NAME)))
                .build();
    }

    @Bean
    public GroupedOpenApi slotsApiGroup() {
        return GroupedOpenApi.builder()
                .group("03-slots")
                .pathsToMatch("/api/slots/**")
                .addOpenApiCustomizer(openApi -> openApi.addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME_NAME)))
                .build();
    }

    @Bean
    public GroupedOpenApi sessionsApiGroup() {
        return GroupedOpenApi.builder()
                .group("04-sessions")
                .pathsToMatch("/api/sessions/**")
                .addOpenApiCustomizer(openApi -> openApi.addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME_NAME)))
                .build();
    }

    @Bean
    public GroupedOpenApi billsApiGroup() {
        return GroupedOpenApi.builder()
                .group("05-bills")
                .pathsToMatch("/api/bills/**")
                .addOpenApiCustomizer(openApi -> openApi.addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME_NAME)))
                .build();
    }

    @Bean
    public GroupedOpenApi usersApiGroup() {
        return GroupedOpenApi.builder()
                .group("06-users")
                .pathsToMatch("/api/users/**")
                .addOpenApiCustomizer(openApi -> openApi.addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME_NAME)))
                .build();
    }

    @Bean
    public GroupedOpenApi adminApiGroup() {
        return GroupedOpenApi.builder()
                .group("07-admin")
                .pathsToMatch("/api/admin/**")
                .addOpenApiCustomizer(openApi -> openApi.addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME_NAME)))
                .build();
    }
}