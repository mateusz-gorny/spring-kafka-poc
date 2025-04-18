package pl.monify.agentgateway.messaging;

import java.util.Map;

public record RegisterAgentMessage(
        String type,
        String correlationId,
        Payload payload
) implements AgentMessage {

    public record Payload(
            String action,
            Map<String, Object> inputSchema,
            Map<String, Object> outputSchema
    ) {}
}
