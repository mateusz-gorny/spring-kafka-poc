package pl.monify.agentgateway.communication.adapter.registry;

import com.fasterxml.jackson.databind.JsonNode;

public record RegisterAgentMessage(String type, Payload payload) {

    public record Payload(String teamId, String action, JsonNode inputSchema, JsonNode outputSchema) {
    }
}
