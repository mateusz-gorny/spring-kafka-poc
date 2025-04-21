package pl.monify.agentgateway.token.adapter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import pl.monify.agentgateway.token.domain.model.JwtAgentClaims;
import pl.monify.agentgateway.token.domain.port.in.JwtTokenParserPort;

import java.util.ArrayList;
import java.util.List;

public class JwtTokenParserAdapter implements JwtTokenParserPort {

    private final JwtParser parser;

    public JwtTokenParserAdapter(JwtParser parser) {
        this.parser = parser;
    }

    @Override
    public JwtAgentClaims parse(String token) {
        Claims claims;
        try {
            claims = parser
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }

        String agentId = claims.getSubject();
        String teamId = claims.get("team_id", String.class);
        String action = claims.get("action", String.class);
        String scope = claims.get("scope", String.class);
        String issuer = claims.getIssuer();
        List<String> audience = claims.getAudience() != null
                ? new ArrayList<>(claims.getAudience())
                : List.of();

        if (agentId == null || teamId == null || action == null) {
            throw new IllegalArgumentException("Missing required claims: sub/team_id/action");
        }

        return new JwtAgentClaims(agentId, teamId, action, scope, issuer, audience);
    }
}
