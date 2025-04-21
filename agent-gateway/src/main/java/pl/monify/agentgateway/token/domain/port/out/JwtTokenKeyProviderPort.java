package pl.monify.agentgateway.token.domain.port.out;

import java.security.Key;

public interface JwtTokenKeyProviderPort {
    Key resolveKey(String keyId);
}
