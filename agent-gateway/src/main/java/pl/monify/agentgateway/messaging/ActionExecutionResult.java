package pl.monify.agentgateway.messaging;

import java.util.List;
import java.util.Map;

public record ActionExecutionResult(
        String type,
        String correlationId,
        Payload payload
) implements AgentMessage {

    public record Payload(
            String status,
            Map<String, Object> output,
            List<String> logs
    ) {}
}
