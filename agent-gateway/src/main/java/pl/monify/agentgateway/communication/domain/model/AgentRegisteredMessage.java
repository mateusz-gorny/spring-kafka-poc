package pl.monify.agentgateway.communication.domain.model;

import com.fasterxml.jackson.databind.JsonNode;

public record AgentRegisteredMessage(
        String agentId,
        String teamId,
        String action,
        String actionType,
        String sessionId,
        JsonNode inputSchema,
        JsonNode outputSchema
) {}
