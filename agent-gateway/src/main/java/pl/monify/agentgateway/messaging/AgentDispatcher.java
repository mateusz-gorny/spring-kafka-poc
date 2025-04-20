package pl.monify.agentgateway.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pl.monify.agentgateway.socket.ActionRegistry;

@Component
public class AgentDispatcher {

    private static final Logger log = LoggerFactory.getLogger(AgentDispatcher.class);

    private final ObjectMapper objectMapper;
    private final ActionRegistry actionRegistry;

    public AgentDispatcher(ObjectMapper objectMapper, ActionRegistry actionRegistry) {
        this.objectMapper = objectMapper;
        this.actionRegistry = actionRegistry;
    }

    public boolean dispatch(ActionExecutionRequest request, String action, String teamId) {
        return actionRegistry.find(teamId, action)
                .map(registered -> {
                    try {
                        String json = objectMapper.writeValueAsString(request);
                        registered.session().sendText(json).subscribe();
                        log.info("[DISPATCH] Sent request {} to agent {}", request.correlationId(), registered.session().getId());
                        return true;
                    } catch (Exception e) {
                        log.error("[DISPATCH] Failed to dispatch message", e);
                        return false;
                    }
                })
                .orElse(false);
    }
}
