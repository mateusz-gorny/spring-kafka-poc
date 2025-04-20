package pl.monify.agentgateway.communication.domain.port.in;

import pl.monify.agentgateway.communication.domain.model.AgentSession;

public interface PingAgentUseCase {
    void pong(AgentSession session);
}
