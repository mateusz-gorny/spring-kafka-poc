package pl.monify.agentstatus.domain.port.out;

import pl.monify.agentstatus.domain.model.AgentPing;

public interface AgentPingPersistencePort {
    void save(AgentPing ping);
}
