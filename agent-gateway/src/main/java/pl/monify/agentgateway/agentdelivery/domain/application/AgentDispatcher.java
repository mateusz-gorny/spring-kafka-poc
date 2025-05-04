package pl.monify.agentgateway.agentdelivery.domain.application;

import pl.monify.agentgateway.agentdelivery.domain.model.ActionExecutionRequestMessage;
import pl.monify.agentgateway.agentdelivery.domain.port.out.AgentSenderPort;
import pl.monify.agentgateway.communication.adapter.registry.RegisteredAction;
import pl.monify.agentgateway.communication.domain.port.out.AgentSessionFinderPort;

import java.util.Optional;

public class AgentDispatcher {

    private final AgentSenderPort sender;
    private final AgentSessionFinderPort agentSessionFinderPort;

    public AgentDispatcher(AgentSenderPort sender, AgentSessionFinderPort agentSessionFinderPort) {
        this.sender = sender;
        this.agentSessionFinderPort = agentSessionFinderPort;
    }

    public void dispatch(ActionExecutionRequestMessage request) {
        Optional<RegisteredAction> registeredAction = agentSessionFinderPort.find(request.teamId(), request.action());
        if (registeredAction.isEmpty()) {
            throw new IllegalArgumentException("No action registered for " + request.action());
        }

        sender.send(request);
    }
}
