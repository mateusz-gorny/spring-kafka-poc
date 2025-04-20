package pl.monify.agentgateway.communication.domain.model;

public record JwtAgentClaims(
        String agentId,
        String teamId,
        String action
) {}
