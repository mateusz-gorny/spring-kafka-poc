package pl.monify.agentgateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for WebSocket connections.
 */
@ConfigurationProperties(prefix = "monify.websocket")
public record WebSocketProperties(
        String path
) {

    public WebSocketProperties {
        if (path == null) {
            path = "/ws/agent";
        }
    }
}