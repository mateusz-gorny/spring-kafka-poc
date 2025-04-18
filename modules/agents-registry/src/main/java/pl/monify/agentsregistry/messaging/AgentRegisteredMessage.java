package pl.monify.agentsregistry.messaging;

import java.util.Map;

public record AgentRegisteredMessage(
        String name,
        String agentId,
        String teamId,
        String displayName,
        String mode, // "MESSAGE" lub "REQUEST"
        String queue,
        Map<String, Object> inputSchema,
        Map<String, Object> outputSchema
) {}
