package pl.monify.workflows.events;

import java.util.Map;

public record ActionExecutionRequestEvent(
        String workflowDefinitionId,
        String workflowInstanceId,
        String action,
        String teamId,
        String correlationId,
        Map<String, Object> input
) {
}
