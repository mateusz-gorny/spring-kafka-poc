package pl.monify.agentgateway.communication.domain.port.out;

import pl.monify.agentgateway.communication.adapter.registry.RegisteredAction;

import java.util.Optional;

public interface AgentSessionFinderPort {
    Optional<RegisteredAction> find(String teamId, String action);
}
