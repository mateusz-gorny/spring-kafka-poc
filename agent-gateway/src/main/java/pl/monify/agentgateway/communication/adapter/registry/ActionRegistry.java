package pl.monify.agentgateway.communication.adapter.registry;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import pl.monify.agentgateway.communication.domain.model.AgentSession;
import pl.monify.agentgateway.communication.domain.port.out.ActionRegistryPort;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ActionRegistry implements ActionRegistryPort {

    private final Map<String, RegisteredAction> registry = new ConcurrentHashMap<>();

    @Override
    public void register(String teamId, String actionName, AgentSession session, JsonNode inputSchema, JsonNode outputSchema) {
        registry.put(teamId + ":" + actionName, new RegisteredAction(session, inputSchema, outputSchema));
    }

    public Optional<RegisteredAction> find(String teamId, String actionName) {
        return Optional.ofNullable(registry.get(teamId + ":" + actionName));
    }
}
