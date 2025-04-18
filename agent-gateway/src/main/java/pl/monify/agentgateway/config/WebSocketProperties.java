package pl.monify.agentgateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for WebSocket connections.
 */
@ConfigurationProperties(prefix = "monify.websocket")
public record WebSocketProperties(
        String path,
        RateLimiting rateLimiting
) {
    /**
     * Rate limiting configuration for WebSocket connections.
     */
    public record RateLimiting(
            int connectionsPerSecond,
            int messagesPerSecond,
            int tokensPerConnection
    ) {
        public RateLimiting {
            if (connectionsPerSecond <= 0) {
                connectionsPerSecond = 10; // Default: 10 connections per second
            }
            if (messagesPerSecond <= 0) {
                messagesPerSecond = 50; // Default: 50 messages per second
            }
            if (tokensPerConnection <= 0) {
                tokensPerConnection = 100; // Default: 100 tokens per connection
            }
        }
    }

    public WebSocketProperties {
        if (path == null) {
            path = "/ws/agent";
        }
        if (rateLimiting == null) {
            rateLimiting = new RateLimiting(10, 50, 100);
        }
    }
}