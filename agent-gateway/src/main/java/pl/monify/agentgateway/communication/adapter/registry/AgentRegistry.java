package pl.monify.agentgateway.communication.adapter.registry;

import org.springframework.stereotype.Component;
import pl.monify.agentgateway.communication.domain.model.AgentSession;
import pl.monify.agentgateway.communication.domain.port.out.FindAgentSessionPort;
import pl.monify.agentgateway.communication.domain.port.out.RegisterAgentSessionPort;
import pl.monify.agentgateway.communication.domain.port.out.UnregisterAgentSessionPort;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AgentRegistry implements
        RegisterAgentSessionPort,
        UnregisterAgentSessionPort,
        FindAgentSessionPort {

    private final Map<String, AgentSession> agents = new ConcurrentHashMap<>();

    @Override
    public void register(String agentId, AgentSession session) {
        agents.put(agentId, session);
    }

    @Override
    public void unregister(String agentId) {
        agents.remove(agentId);
    }

    @Override
    public Optional<AgentSession> findById(String agentId) {
        return Optional.ofNullable(agents.get(agentId));
    }
}
