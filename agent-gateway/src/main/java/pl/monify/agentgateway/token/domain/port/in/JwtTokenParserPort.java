package pl.monify.agentgateway.token.domain.port.in;

import pl.monify.agentgateway.token.domain.model.JwtAgentClaims;

public interface JwtTokenParserPort {
    JwtAgentClaims parse(String token);
}
