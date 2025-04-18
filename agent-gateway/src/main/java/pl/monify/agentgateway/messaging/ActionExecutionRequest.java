package pl.monify.agentgateway.messaging;

import java.util.Map;

public record ActionExecutionRequest(
        String type,
        String correlationId,
        Payload payload
) implements AgentMessage {

    public record Payload(
            String workflowInstanceId,
            String action,
            Map<String, Object> input
    ) {}
}
