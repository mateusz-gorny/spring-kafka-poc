package pl.monify.workflows.application;

import pl.monify.workflows.domain.WorkflowInstance;
import pl.monify.workflows.events.WorkflowActionResponseEvent;

public interface WorkflowStateUpdater {
    WorkflowInstance applyStep(WorkflowInstance instance, WorkflowActionResponseEvent event);
}
