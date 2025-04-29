package pl.monify.agentgateway.communication.domain.model;

import com.fasterxml.jackson.databind.JsonNode;

public record AgentRegisterModel(String agentId, String teamId, String actionName, AgentSession session, JsonNode inputSchema, JsonNode outputSchema) {
}
