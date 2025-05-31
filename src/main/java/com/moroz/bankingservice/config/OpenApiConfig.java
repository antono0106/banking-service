package com.moroz.bankingservice.config;

import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openApi() throws IOException {
        final String openApiYaml = StreamUtils.copyToString(
                new ClassPathResource("/openapi.yaml").getInputStream(), StandardCharsets.UTF_8
        );
        return Yaml.mapper().readValue(openApiYaml, OpenAPI.class);
    }
}
