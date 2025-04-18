package pl.monify.agentgateway.messaging;

import java.util.Map;

public record AgentRegisteredMessage(
        String name,
        String agentId,
        String teamId,
        Map<String, Object> inputSchema,
        Map<String, Object> outputSchema
) {}
