package pl.monify.agentgateway.token.domain.port.out;

import pl.monify.agentgateway.token.domain.model.JwtAgentClaims;

public interface JwtTokenGeneratorPort {
    String generate(JwtAgentClaims claims);
}
