package pl.monify.workflows.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.monify.workflows.model.WorkflowInstance;

import java.util.List;

public interface WorkflowInstanceRepository extends MongoRepository<WorkflowInstance, String> {
    List<WorkflowInstance> findByWorkflowIdOrderByStartedAtDesc(String workflowId);
}
