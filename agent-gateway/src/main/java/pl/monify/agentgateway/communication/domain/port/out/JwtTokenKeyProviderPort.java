package pl.monify.agentgateway.communication.domain.port.out;

import java.security.Key;

public interface JwtTokenKeyProviderPort {
    Key resolveKey(String keyId);
}
