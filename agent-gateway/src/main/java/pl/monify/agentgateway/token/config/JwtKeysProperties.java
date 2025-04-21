package pl.monify.agentgateway.token.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "monify.jwt")
public record JwtKeysProperties(Map<String, String> keys, String issuer, long tokenTtlSeconds) {
}

