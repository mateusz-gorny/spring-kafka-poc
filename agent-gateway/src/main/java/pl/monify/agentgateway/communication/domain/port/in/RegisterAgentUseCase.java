package pl.monify.agentgateway.communication.domain.port.in;

import com.fasterxml.jackson.databind.JsonNode;
import pl.monify.agentgateway.communication.domain.model.AgentSession;

public interface RegisterAgentUseCase {
    void register(String teamId, String actionName, AgentSession session, JsonNode inputSchema, JsonNode outputSchema);
}
