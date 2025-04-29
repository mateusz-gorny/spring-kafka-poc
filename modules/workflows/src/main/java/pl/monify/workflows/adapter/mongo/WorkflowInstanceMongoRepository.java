package pl.monify.workflows.adapter.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface WorkflowInstanceMongoRepository
        extends MongoRepository<WorkflowInstanceDocument, String> {
    List<WorkflowInstanceDocument> findByDefinitionId(String definitionId);
}
