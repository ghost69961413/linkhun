package com.linkhub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.beans.factory.annotation.Value;
import java.util.Arrays;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${linkhub.upload-dir:uploads}")
    private String uploadDirectory;

    @Value("${linkhub.cors.allowed-origins:http://localhost:3000,http://localhost:5173,http://localhost:5174,http://localhost:5175,http://127.0.0.1:5174,http://127.0.0.1:5175}")
    private String allowedOrigins;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = java.nio.file.Path.of(uploadDirectory).toAbsolutePath().normalize().toUri().toString();
        if (!location.endsWith("/")) location += "/";
        registry.addResourceHandler("/uploads/**").addResourceLocations(location);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")
                .allowedOrigins(Arrays.stream(allowedOrigins.split(","))
                        .map(String::trim)
                        .filter(origin -> !origin.isEmpty())
                        .toArray(String[]::new))
                .allowedMethods(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
