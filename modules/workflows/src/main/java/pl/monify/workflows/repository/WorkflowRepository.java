package pl.monify.workflows.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.monify.workflows.model.WorkflowDefinition;

import java.util.List;

public interface WorkflowRepository extends MongoRepository<WorkflowDefinition, String> {
    List<WorkflowDefinition> findByStatusNot(WorkflowDefinition.WorkflowStatus status);
}
