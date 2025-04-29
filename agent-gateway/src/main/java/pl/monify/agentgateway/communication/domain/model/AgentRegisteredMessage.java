package pl.monify.agentgateway.communication.domain.model;

import com.fasterxml.jackson.databind.JsonNode;

public record AgentRegisteredMessage(
        String agentId,
        String action,
        String sessionId,
        String teamId,
        JsonNode inputSchema,
        JsonNode outputSchema
) {}
