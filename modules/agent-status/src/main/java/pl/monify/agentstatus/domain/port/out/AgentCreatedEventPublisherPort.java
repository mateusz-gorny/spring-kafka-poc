package pl.monify.agentstatus.domain.port.out;

import pl.monify.agentstatus.domain.event.AgentCreatedEvent;

public interface AgentCreatedEventPublisherPort {
    void publish(AgentCreatedEvent event);
}
