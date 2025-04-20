package pl.monify.agentgateway.communication.domain.port.out;

import pl.monify.agentgateway.communication.domain.model.AgentRegisteredMessage;

public interface AgentRegisteredEventSenderPort {
    void send(AgentRegisteredMessage message);
}
