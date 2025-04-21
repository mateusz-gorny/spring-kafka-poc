package pl.monify.agentgateway.communication.adapter.registry;

import com.fasterxml.jackson.databind.JsonNode;

public record RegisterAgentMessage(String type, String correlationId, String teamId, String action, JsonNode inputSchema, JsonNode outputSchema) {
}
