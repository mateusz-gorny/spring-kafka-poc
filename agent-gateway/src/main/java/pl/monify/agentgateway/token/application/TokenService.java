package pl.monify.agentgateway.token.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.monify.agentgateway.token.config.JwtKeysProperties;
import pl.monify.agentgateway.token.domain.model.JwtAgentClaims;
import pl.monify.agentgateway.token.domain.port.out.JwtTokenGeneratorPort;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;

public class TokenService {

    private static final Logger log = LoggerFactory.getLogger(TokenService.class);
    private final JwtTokenGeneratorPort tokenGenerator;
    private final JwtKeysProperties keysProperties;

    public TokenService(JwtTokenGeneratorPort tokenGenerator, JwtKeysProperties keysProperties) {
        this.tokenGenerator = tokenGenerator;
        this.keysProperties = keysProperties;
    }

    public String generateToken(String agentId, String secretBase64, String teamId, List<String> actions) {
        log.info("Generating token for agent {} for team {} with actions {}", agentId, teamId, actions);
        String expectedBase64 = keysProperties.keys().get(agentId);
        if (expectedBase64 == null) {
            throw new SecurityException("Unknown agent: " + agentId);
        }

        byte[] expected = Base64.getDecoder().decode(expectedBase64);
        byte[] provided = Base64.getDecoder().decode(secretBase64);

        log.info("Checking provided secret with expected");
        if (!MessageDigest.isEqual(expected, provided)) {
            throw new SecurityException("Invalid secret for agent: " + agentId);
        }

        log.info("Secret is valid, generating token for agent {} for team {} with actions {}", agentId, teamId, actions);
        JwtAgentClaims claims = new JwtAgentClaims(
                agentId,
                teamId,
                String.join(" ", actions),
                buildScope(actions),
                keysProperties.issuer(),
                List.of(
                        getClass().getPackageName().split("\\.token")[0]
                )
        );

        log.info("Token generated for agent {} for team {} with actions {}", agentId, teamId, actions);
        return tokenGenerator.generate(claims);
    }

    private String buildScope(List<String> actions) {
        StringBuilder sb = new StringBuilder("agent:ping agent:result");
        for (String action : actions) {
            sb.append(" agent:").append(action.trim());
        }
        return sb.toString().trim();
    }
}
