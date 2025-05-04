package pl.monify.agentgateway.communication.adapter.registry;

import com.fasterxml.jackson.databind.JsonNode;

public record RegisterAgentMessage(
        String agentId,
        String teamId,
        String action,
        String type,
        String correlationId,
        JsonNode inputSchema,
        JsonNode outputSchema,
        String actionType,
        String ttl
) {}
