package pl.monify.agentgateway.socket;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AgentRegistry {

    private final Map<String, AgentSession> agents = new ConcurrentHashMap<>();

    public void register(String agentId, AgentSession session) {
        agents.put(agentId, session);
    }

    public void unregister(String agentId) {
        agents.remove(agentId);
    }

    public Optional<AgentSession> findById(String agentId) {
        return Optional.ofNullable(agents.get(agentId));
    }
}
