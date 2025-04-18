package pl.monify.agentgateway.messaging;

public record ErrorMessage(
        String type,
        String correlationId,
        Payload payload
) implements AgentMessage {

    public record Payload(
            String message,
            String code
    ) {}
}
