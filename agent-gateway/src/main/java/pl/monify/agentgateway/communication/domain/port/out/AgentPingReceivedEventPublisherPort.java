package pl.monify.agentgateway.communication.domain.port.out;

import pl.monify.agentgateway.communication.domain.event.AgentPingReceivedEvent;

public interface AgentPingReceivedEventPublisherPort {
    void publish(AgentPingReceivedEvent event);
}
