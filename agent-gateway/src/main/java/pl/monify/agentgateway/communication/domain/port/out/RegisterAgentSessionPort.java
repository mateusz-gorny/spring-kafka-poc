package pl.monify.agentgateway.communication.domain.port.out;

import pl.monify.agentgateway.communication.domain.model.AgentSession;

public interface RegisterAgentSessionPort {
    void register(String agentId, AgentSession session);
}
