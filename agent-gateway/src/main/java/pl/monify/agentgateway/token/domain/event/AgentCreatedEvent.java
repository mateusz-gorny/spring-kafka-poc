package pl.monify.agentgateway.token.domain.event;

public record AgentCreatedEvent(
        String agentId,
        String secret,
        String name
) {}
