package pl.monify.workflows.port.out;

import pl.monify.workflows.domain.WorkflowDefinition;
import java.util.List;
import java.util.Optional;

public interface WorkflowDefinitionRepository {
    Optional<WorkflowDefinition> findById(String id);
    Optional<WorkflowDefinition> findByCorrelationId(String correlationId);
    List<WorkflowDefinition> findAll();
    WorkflowDefinition save(WorkflowDefinition definition);
    void delete(String id);
}
