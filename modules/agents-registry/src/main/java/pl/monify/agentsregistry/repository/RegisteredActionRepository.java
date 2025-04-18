package pl.monify.agentsregistry.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.monify.agentsregistry.model.RegisteredActionInstance;

import java.util.List;

public interface RegisteredActionRepository extends MongoRepository<RegisteredActionInstance, String> {
    List<RegisteredActionInstance> findByNameAndActiveIsTrue(String name);
}
