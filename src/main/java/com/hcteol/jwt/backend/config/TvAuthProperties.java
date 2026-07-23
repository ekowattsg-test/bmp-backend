package com.hcteol.jwt.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "tv.auth")
public class TvAuthProperties {

    private long challengeExpirySeconds = 120;
    private long sessionMaxSeconds = 28800;
    private long exchangeCodeExpirySeconds = 60;
    private long pollIntervalSeconds = 2;
    private long refreshIntervalSeconds = 30;
    private boolean requirePin = true;
    private String defaultDestinationUrl = "/tv/projects";
    private String qrSchemeBase = "bmp://tv-auth?code=";
}
