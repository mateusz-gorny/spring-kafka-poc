package pl.monify.workflows.events;

import java.time.Instant;
import java.util.Map;

public record WorkflowActionResponseEvent(
        String workflowInstanceId,
        String workflowDefinitionId,
        String correlationId,
        String actionName,
        String agentId,
        String status,
        Map<String, Object> output,
        String[] logs,
        Instant timestamp,
        Map<String, String> metadata
) {}
