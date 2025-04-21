package pl.monify.agentgateway.communication.application;

import com.fasterxml.jackson.databind.JsonNode;
import pl.monify.agentgateway.communication.domain.model.AgentRegisteredMessage;
import pl.monify.agentgateway.communication.domain.model.AgentSession;
import pl.monify.agentgateway.communication.domain.port.in.RegisterAgentUseCase;
import pl.monify.agentgateway.communication.domain.port.out.ActionRegistryPort;
import pl.monify.agentgateway.communication.domain.port.out.AgentRegisteredEventSenderPort;

public class RegisterAgentService implements RegisterAgentUseCase {

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

        AgentRegisteredMessage event = new AgentRegisteredMessage(action, session.id(), teamId, inputSchema, outputSchema);
        eventSender.send(event);
    }
}
