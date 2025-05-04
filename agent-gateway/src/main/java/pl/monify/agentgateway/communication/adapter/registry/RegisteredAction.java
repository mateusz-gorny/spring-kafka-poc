package pl.monify.agentgateway.communication.adapter.registry;

import com.fasterxml.jackson.databind.JsonNode;
import pl.monify.agentgateway.agentdelivery.domain.model.ActionType;
import pl.monify.agentgateway.communication.domain.model.AgentSession;

import java.time.Duration;

public record RegisteredAction(
        AgentSession session,
        JsonNode inputSchema,
        JsonNode outputSchema,
        ActionType actionType,
        Duration ttl
) {}
