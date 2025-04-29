package pl.monify.workflows.application;

import pl.monify.workflows.domain.WorkflowInstance;

public interface WorkflowInstanceFactory {
    WorkflowInstance loadOrCreate(String correlationId, String workflowDefinitionId);
}
