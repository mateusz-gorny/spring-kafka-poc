package pl.monify.agentgateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for JWT authentication.
 */
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secret
) {
}