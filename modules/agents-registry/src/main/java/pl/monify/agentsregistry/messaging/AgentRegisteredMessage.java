package pl.monify.agentsregistry.messaging;

import java.util.Map;

public record AgentRegisteredMessage(
        String agentId,
        String action,
        String sessionId,
        String teamId,
        String displayName,
        String mode,
        String queue,
        Map<String, Object> inputSchema,
        Map<String, Object> outputSchema
) {}
