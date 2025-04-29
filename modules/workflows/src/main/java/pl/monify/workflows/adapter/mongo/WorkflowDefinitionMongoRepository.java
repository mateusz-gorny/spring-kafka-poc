package pl.monify.workflows.adapter.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.monify.workflows.domain.WorkflowDefinition;

import java.util.Optional;

public interface WorkflowDefinitionMongoRepository extends MongoRepository<WorkflowDefinition, String> {
    Optional<WorkflowDefinition> findByCorrelationId(String correlationId);
}
