package pl.monify.agentgateway.communication.domain.model;

import com.fasterxml.jackson.databind.JsonNode;

public record AgentRegisteredMessage(
        String action,
        String agentId,
        String teamId,
        JsonNode inputSchema,
        JsonNode outputSchema
) {}
