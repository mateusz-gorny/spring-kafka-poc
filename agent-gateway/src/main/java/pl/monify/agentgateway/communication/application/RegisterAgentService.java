package pl.monify.agentgateway.communication.application;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.monify.agentgateway.communication.domain.model.AgentRegisteredMessage;
import pl.monify.agentgateway.communication.domain.model.AgentSession;
import pl.monify.agentgateway.communication.domain.port.in.RegisterAgentUseCase;
import pl.monify.agentgateway.communication.domain.port.out.ActionRegistryPort;
import pl.monify.agentgateway.communication.domain.port.out.AgentRegisteredEventSenderPort;

public class RegisterAgentService implements RegisterAgentUseCase {

    private static final Logger log = LoggerFactory.getLogger(RegisterAgentService.class);

    private final ActionRegistryPort actionRegistry;
    private final AgentRegisteredEventSenderPort eventSender;

    public RegisterAgentService(ActionRegistryPort actionRegistry,
                                AgentRegisteredEventSenderPort eventSender) {
        this.actionRegistry = actionRegistry;
        this.eventSender = eventSender;
    }

    @Override
    public void register(String teamId, String action, AgentSession session, JsonNode inputSchema, JsonNode outputSchema) {
        actionRegistry.register(teamId, action, session, inputSchema, outputSchema);
        log.info("[WS] Registered action '{}' for agent {}", action, session.id());

        AgentRegisteredMessage event = new AgentRegisteredMessage(action, session.id(), teamId, inputSchema, outputSchema);
        eventSender.send(event);
    }
}
