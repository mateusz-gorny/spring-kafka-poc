package pl.monify.agentgateway.communication.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import pl.monify.agentgateway.agentdelivery.domain.model.ActionType;

import java.time.Duration;

public record AgentRegisterModel(
        String agentId,
        String teamId,
        String actionName,
        AgentSession session,
        JsonNode inputSchema,
        JsonNode outputSchema,
        ActionType actionType,
        Duration ttl
) {}
