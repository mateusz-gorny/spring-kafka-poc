package pl.monify.application;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.monify.agentstoken.AgentTokenService;

import javax.crypto.SecretKey;
import java.io.Serial;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {
    private static final Logger log = LoggerFactory.getLogger(JwtService.class);
    private final SecretKey secretKey;
    private final AgentTokenService agentTokenService;

    public JwtService(SecretKey secretKey, AgentTokenService agentTokenService) {
        this.secretKey = secretKey;
        this.agentTokenService = agentTokenService;
    }

    public String generateToken(User user, String teamId) {
        Instant now = Instant.now();

        return Jwts.builder()
                .claims()
                .subject(user.getUsername())
                .add("authorities", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .add("team_id", teamId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(3600)))
                .and()
                .signWith(secretKey)
                .compact();
    }

    public String generateTokenForAgent(String agentId) {
        log.info("[AGENT] Generating JWT for agent {}", agentId);
        Instant now = Instant.now();
        log.info("[AGENT] JWT will expire in 15 minutes");

        return Jwts.builder()
                .claims()
                .subject(agentId)
                .add("agent", true)
                .add("team_id", "FirstTeam")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(900)))
                .and()
                .signWith(secretKey)
                .compact();
    }

    public boolean isValidAgentSecret(String agentId, String secret) {
        return agentTokenService.isValidSecret(agentId, secret);
    }

    public User parseToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException error) {
            log.error("Invalid token: {}", token, error);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        String username = claims.getSubject();
        List<String> authorities = claims.get("authorities", List.class);

        return new JwtUser(
                username,
                "",
                "FirstTeam",
                authorities.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList()
        );
    }

    static class JwtUser extends User {
        @Serial
        private static final long serialVersionUID = 1L;

        private final String teamId;

        public JwtUser(String username, String password, String teamId, List<SimpleGrantedAuthority> authorities) {
            super(username, password, authorities);
            this.teamId = teamId;
        }

        public String getTeamId() {
            return teamId;
        }
    }
}
