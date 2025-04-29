package pl.monify.workflows.port.out;

import pl.monify.workflows.events.ActionExecutionRequestEvent;

public interface ActionExecutionRequestPublisher {
    void publish(ActionExecutionRequestEvent event);
}
