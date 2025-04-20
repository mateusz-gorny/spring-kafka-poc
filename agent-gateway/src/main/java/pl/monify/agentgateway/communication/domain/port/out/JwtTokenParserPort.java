package pl.monify.agentgateway.communication.domain.port.out;

import pl.monify.agentgateway.communication.domain.model.JwtAgentClaims;

public interface JwtTokenParserPort {
    JwtAgentClaims parse(String token);
}
