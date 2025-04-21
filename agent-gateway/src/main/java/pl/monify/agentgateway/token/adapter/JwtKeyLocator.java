package pl.monify.agentgateway.token.adapter;

import io.jsonwebtoken.LocatorAdapter;
import io.jsonwebtoken.ProtectedHeader;
import pl.monify.agentgateway.token.domain.port.out.JwtTokenKeyProviderPort;

import java.security.Key;

public class JwtKeyLocator extends LocatorAdapter<Key> {

    private final JwtTokenKeyProviderPort provider;

    public JwtKeyLocator(JwtTokenKeyProviderPort provider) {
        this.provider = provider;
    }

    @Override
    public Key locate(ProtectedHeader header) {
        String kid = header.getKeyId();
        if (kid == null || kid.isBlank()) {
            throw new IllegalArgumentException("Missing 'kid' in JWT header");
        }
        return provider.resolveKey(kid);
    }
}
