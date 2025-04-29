package pl.monify.agentstatus.adapter.mongo;

import pl.monify.agentstatus.domain.model.AgentPing;
import pl.monify.agentstatus.domain.port.out.AgentPingPersistencePort;

public class AgentPingPersistenceAdapter implements AgentPingPersistencePort {

    private final AgentPingRepository repository;

    public AgentPingPersistenceAdapter(AgentPingRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(AgentPing ping) {
        repository.save(AgentPingDocument.from(ping));
    }
}
