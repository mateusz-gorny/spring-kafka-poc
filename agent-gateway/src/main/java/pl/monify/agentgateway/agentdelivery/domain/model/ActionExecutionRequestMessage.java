package pl.monify.agentgateway.agentdelivery.domain.model;

import java.util.Map;

public record ActionExecutionRequestMessage(
        String workflowInstanceId,
        String action,
        String teamId,
        String correlationId,
        Map<String, Object> input
) {}
