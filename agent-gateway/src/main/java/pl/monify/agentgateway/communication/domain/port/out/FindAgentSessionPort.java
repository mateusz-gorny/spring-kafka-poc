package pl.monify.agentgateway.communication.domain.port.out;

import pl.monify.agentgateway.communication.domain.model.AgentSession;

import java.util.Optional;

public interface FindAgentSessionPort {
    Optional<AgentSession> findById(String agentId);
}
