package pl.monify.agent.model;

import java.util.Map;

public record ActionExecutionRequestMessageModel(
        String type,
        String workflowDefinitionId,
        String workflowInstanceId,
        String action,
        String teamId,
        String correlationId,
        Map<String, Object> input
) {}
