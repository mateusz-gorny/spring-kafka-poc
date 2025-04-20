package pl.monify.agentgateway.communication.adapter.registry;

import com.fasterxml.jackson.databind.JsonNode;
import pl.monify.agentgateway.communication.domain.model.AgentSession;

public record RegisteredAction(
        AgentSession session,
        JsonNode inputSchema,
        JsonNode outputSchema
) {
}
