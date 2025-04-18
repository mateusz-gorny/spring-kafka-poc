package pl.monify.agentgateway.messaging;

public record PingMessage(String type, String correlationId, Object payload) implements AgentMessage {
}
