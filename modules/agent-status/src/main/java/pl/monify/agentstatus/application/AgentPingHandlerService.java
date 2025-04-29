package pl.monify.agentstatus.application;

import pl.monify.agentstatus.domain.model.AgentPing;
import pl.monify.agentstatus.domain.port.out.AgentPingPersistencePort;

public class AgentPingHandlerService {

    private final AgentPingPersistencePort persistencePort;

    public AgentPingHandlerService(AgentPingPersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }

    public void handle(AgentPing ping) {
        persistencePort.save(ping);
    }
}
