package pl.monify.agentgateway.socket;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ActionRegistry {

    public record RegisteredAction(
            String actionName,
            AgentSession session,
            Map<String, Object> inputSchema,
            Map<String, Object> outputSchema
    ) {}

    private final Map<String, RegisteredAction> actions = new ConcurrentHashMap<>();

    public void register(String teamId, String actionName, AgentSession session,
                         Map<String, Object> inputSchema, Map<String, Object> outputSchema) {
        String key = key(teamId, actionName);
        actions.put(key, new RegisteredAction(actionName, session, inputSchema, outputSchema));
    }

    public Optional<RegisteredAction> find(String teamId, String actionName) {
        return Optional.ofNullable(actions.get(key(teamId, actionName)));
    }

    private String key(String teamId, String actionName) {
        return teamId + "::" + actionName;
    }
}
