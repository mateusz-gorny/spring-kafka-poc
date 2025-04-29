package pl.monify.agentgateway.token.adapter.mongo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.monify.agentgateway.token.domain.port.out.JwtTokenKeyProviderPort;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

public class MongoJwtKeyProvider implements JwtTokenKeyProviderPort {

    private static final Logger log = LoggerFactory.getLogger(MongoJwtKeyProvider.class);
    private final MongoAgentKeyRepository repository;

    public MongoJwtKeyProvider(MongoAgentKeyRepository repository) {
        this.repository = repository;
    }

    @Override
    public Key resolveKey(String keyId) {
        log.debug("Resolving JWT key for agentId={}", keyId);
        return repository.findByAgentId(keyId)
                .map(doc -> {
                    byte[] decoded = Base64.getDecoder().decode(doc.getSecret());
                    return new SecretKeySpec(decoded, "HmacSHA256");
                })
                .orElseThrow(() -> new IllegalArgumentException("Unknown agent ID: " + keyId));
    }
}
