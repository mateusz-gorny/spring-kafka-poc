package pl.monify.agentstatus.domain.event;

public record AgentCreatedEvent(
        String agentId,
        String secret,
        String name
) {}
