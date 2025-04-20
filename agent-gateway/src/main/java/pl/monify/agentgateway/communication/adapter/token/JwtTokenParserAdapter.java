package pl.monify.agentgateway.communication.adapter.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import pl.monify.agentgateway.communication.domain.model.JwtAgentClaims;
import pl.monify.agentgateway.communication.domain.port.out.JwtTokenParserPort;

public class JwtTokenParserAdapter implements JwtTokenParserPort {

    private final JwtParser jwtParser;

    public JwtTokenParserAdapter(JwtParser jwtParser) {
        this.jwtParser = jwtParser;
    }

    @Override
    public JwtAgentClaims parse(String token) {
        try {
            Claims claims = jwtParser.parseSignedClaims(token).getPayload();

            Boolean isAgent = claims.get("agent", Boolean.class);
            if (isAgent == null || !isAgent) {
                throw new JwtException("Invalid or missing 'agent' claim");
            }

            String agentId = claims.getSubject();
            if (agentId == null || agentId.isBlank()) {
                throw new JwtException("Missing subject (agentId) in token");
            }

            String teamId = claims.get("team_id", String.class);
            if (teamId == null || teamId.isBlank()) {
                throw new JwtException("Missing 'team_id' claim");
            }

            String action = claims.get("action", String.class);
            if (action == null || action.isBlank()) {
                throw new JwtException("Missing 'action' claim");
            }

            return new JwtAgentClaims(agentId, teamId, action);

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token: " + e.getMessage(), e);
        }
    }
}
