package pl.monify.agentgateway.token.domain.model;

import java.util.List;

public record JwtAgentClaims(
        String agentId,
        String teamId,
        String action,
        String scope,
        String issuer,
        List<String> audience
) {}
