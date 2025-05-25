package com.juan.spring.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class JwtProperties {
    private String jwtSecret;
    private int jwtExpirationMilliseconds;

    public String getJwtSecret() {
        return jwtSecret;
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public int getJwtExpirationMilliseconds() {
        return jwtExpirationMilliseconds;
    }

    public void setJwtExpirationMilliseconds(int jwtExpirationMilliseconds) {
        this.jwtExpirationMilliseconds = jwtExpirationMilliseconds;
    }
} 