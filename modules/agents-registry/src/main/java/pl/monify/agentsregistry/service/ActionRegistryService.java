package pl.monify.agentsregistry.service;

import org.springframework.stereotype.Service;
import pl.monify.agentsregistry.model.RegisteredActionInstance;
import pl.monify.agentsregistry.repository.RegisteredActionRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ActionRegistryService {

    private final RegisteredActionRepository repository;

    public ActionRegistryService(RegisteredActionRepository repository) {
        this.repository = repository;
    }

    public void register(RegisteredActionInstance instance) {
        instance.setRegisteredAt(Instant.now());
        instance.setLastHealthCheck(Instant.now());
        instance.setActive(true);
        repository.save(instance);
    }

    public List<RegisteredActionInstance> findActiveByName(String name) {
        return repository.findByNameAndActiveIsTrue(name);
    }

    public Optional<RegisteredActionInstance> findByAgentId(String agentId) {
        return repository.findByAgentId(agentId);
    }

    // TODO: Right now we have ping agent functionality which set agent as live. We should have something similar here. Right now isActive is set only on register so we shouldn't rely on that.
//    @Scheduled(fixedRate = 60000)
//    public void deactivateStaleActions() {
//        Instant cutoff = Instant.now().minusSeconds(120);
//        List<RegisteredActionInstance> all = repository.findAll();
//
//        all.stream()
//                .filter(a -> a.getLastHealthCheck() != null && a.getLastHealthCheck().isBefore(cutoff))
//                .filter(RegisteredActionInstance::isActive)
//                .forEach(a -> {
//                    a.setActive(false);
//                    repository.save(a);
//                });
//    }
}
