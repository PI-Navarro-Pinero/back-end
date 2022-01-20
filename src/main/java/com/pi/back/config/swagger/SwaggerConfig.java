package com.pi.back.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    public static final String TITLE = "PINP REST API";
    public static final String DESCRIPTION = "Proyecto Integrador Navarro-Piñero";
    public static final String BOOKINGS_BASIC_AUTH = "'Basic' HTTP Authentication Scheme - RFC 7617";
    public static final String BASIC_AUTH = "basic";

    @Bean
    public OpenAPI buildOpenApi(ObjectProvider<SecurityScheme> securitySchemeProvider) {
        OpenAPI openApi = new OpenAPI();

        Components components = new Components();
        securitySchemeProvider.ifAvailable(scp -> components.addSecuritySchemes(BOOKINGS_BASIC_AUTH, scp));

        openApi.setComponents(components);

        return openApi.info(buildApiInfo());
    }

    @Bean
    public SecurityScheme getBookingsAuthSchema() {
        return new SecurityScheme()
                .name(BOOKINGS_BASIC_AUTH)
                .type(SecurityScheme.Type.HTTP)
                .scheme(BASIC_AUTH);
    }

    Info buildApiInfo() {
        return new Info()
                .title(TITLE)
                .description(DESCRIPTION);
    }
}
