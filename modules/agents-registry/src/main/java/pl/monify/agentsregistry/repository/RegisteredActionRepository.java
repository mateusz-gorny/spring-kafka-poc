package pl.monify.agentsregistry.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.monify.agentsregistry.model.RegisteredActionInstance;

import java.util.List;
import java.util.Optional;

public interface RegisteredActionRepository extends MongoRepository<RegisteredActionInstance, String> {
    List<RegisteredActionInstance> findByNameAndActiveIsTrue(String name);

    Optional<RegisteredActionInstance> findByAgentId(String agentId);
}
