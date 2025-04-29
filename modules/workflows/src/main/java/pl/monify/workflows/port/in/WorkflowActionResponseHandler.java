package pl.monify.workflows.port.in;

import pl.monify.workflows.events.WorkflowActionResponseEvent;

public interface WorkflowActionResponseHandler {
    void handle(WorkflowActionResponseEvent event);
}
