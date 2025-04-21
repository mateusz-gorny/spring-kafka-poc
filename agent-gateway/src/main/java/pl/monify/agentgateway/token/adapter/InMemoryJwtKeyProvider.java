package pl.monify.agentgateway.token.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.monify.agentgateway.token.domain.port.out.JwtTokenKeyProviderPort;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryJwtKeyProvider implements JwtTokenKeyProviderPort {

    private static final Logger log = LoggerFactory.getLogger(InMemoryJwtKeyProvider.class);
    private final Map<String, SecretKey> keys = new ConcurrentHashMap<>();

    public InMemoryJwtKeyProvider(Map<String, String> rawKeys) {
        log.info("Loading keys from {}", rawKeys);

        rawKeys.forEach((kid, base64Secret) -> {
            byte[] decoded = Base64.getDecoder().decode(base64Secret);
            keys.put(kid, new SecretKeySpec(decoded, "HmacSHA256"));
        });
    }

    @Override
    public Key resolveKey(String keyId) {
        if (!keys.containsKey(keyId)) {
            throw new IllegalArgumentException("Unknown key ID: " + keyId);
        }
        return keys.get(keyId);
    }
}
