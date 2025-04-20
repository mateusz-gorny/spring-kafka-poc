package pl.monify.agentgateway.communication.domain.port.out;

import com.fasterxml.jackson.databind.JsonNode;
import pl.monify.agentgateway.communication.domain.model.AgentSession;

public interface ActionRegistryPort {
    void register(String teamId, String actionName, AgentSession session, JsonNode inputSchema, JsonNode outputSchema);
}
