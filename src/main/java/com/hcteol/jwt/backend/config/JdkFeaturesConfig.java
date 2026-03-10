package com.hcteol.jwt.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Configuration class showcasing JDK 21 features
 */
@Configuration
public class JdkFeaturesConfig {

    private final Environment environment;

    public JdkFeaturesConfig(Environment environment) {
        this.environment = environment;
    }

    /**
     * Example method using JDK 21 switch expressions and pattern matching
     */
    public String getEnvironmentInfo() {
        var profiles = environment.getActiveProfiles();
        return switch (profiles.length) {
            case 0 -> "No active profiles - using default configuration";
            case 1 -> "Single profile active: " + profiles[0];
            default -> "Multiple profiles active: " + String.join(", ", profiles);
        };
    }

    /**
     * Example using text blocks for better SQL readability
     */
    public String getApplicationInfo() {
        return """
                Application: Spring Boot JWT Backend
                Java Version: %s
                Spring Boot: Enhanced with JDK 21 features
                Features: Records, Pattern Matching, Text Blocks
                """.formatted(System.getProperty("java.version"));
    }
}
