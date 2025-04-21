package pl.monify.agentgateway.token.adapter;

import io.jsonwebtoken.Jwts;
import pl.monify.agentgateway.token.domain.model.JwtAgentClaims;
import pl.monify.agentgateway.token.domain.port.out.JwtTokenGeneratorPort;
import pl.monify.agentgateway.token.domain.port.out.JwtTokenKeyProviderPort;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

public class JwtTokenGeneratorAdapter implements JwtTokenGeneratorPort {

    private final JwtTokenKeyProviderPort keyProvider;
    private final String issuer;
    private final long tokenTtlSeconds;

    public JwtTokenGeneratorAdapter(JwtTokenKeyProviderPort keyProvider, String issuer, long tokenTtlSeconds) {
        this.keyProvider = keyProvider;
        this.issuer = issuer;
        this.tokenTtlSeconds = tokenTtlSeconds;
    }

    @Override
    public String generate(JwtAgentClaims claims) {
        String keyId = claims.agentId();

        Key key = keyProvider.resolveKey(keyId);
        if (!(key instanceof SecretKey secretKey)) {
            throw new IllegalArgumentException("Expected HMAC SecretKey for keyId=" + keyId +
                    ", but got: " + key.getClass().getSimpleName());
        }

        Instant now = Instant.now();
        Instant exp = now.plusSeconds(tokenTtlSeconds);

        return Jwts.builder()
                .header()
                .keyId(keyId)
                .and()
                .subject(claims.agentId())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claim("team_id", claims.teamId())
                .claim("action", claims.action())
                .claim("scope", claims.scope())
                .claim("aud", String.join(" ", claims.audience()))
                .signWith(secretKey)
                .compact();
    }
}
