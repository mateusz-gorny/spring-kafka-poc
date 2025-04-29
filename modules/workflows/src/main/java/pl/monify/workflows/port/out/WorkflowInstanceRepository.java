package pl.monify.workflows.port.out;

import pl.monify.workflows.domain.WorkflowInstance;
import java.util.List;
import java.util.Optional;

public interface WorkflowInstanceRepository {
    Optional<WorkflowInstance> findById(String workflowInstanceId);
    List<WorkflowInstance> findAll();
    List<WorkflowInstance> findByDefinitionId(String definitionId);
    void save(WorkflowInstance instance);
}
