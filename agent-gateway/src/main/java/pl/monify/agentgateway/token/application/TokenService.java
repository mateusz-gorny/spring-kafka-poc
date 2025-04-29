package pl.monify.agentgateway.token.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.monify.agentgateway.token.domain.model.JwtAgentClaims;
import pl.monify.agentgateway.token.domain.port.out.JwtTokenGeneratorPort;
import pl.monify.agentgateway.token.domain.port.out.JwtTokenKeyProviderPort;

import javax.crypto.SecretKey;
import java.security.Key;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;

public class TokenService {

    private static final Logger log = LoggerFactory.getLogger(TokenService.class);
    private final JwtTokenGeneratorPort tokenGenerator;
    private final JwtTokenKeyProviderPort keyProvider;

    public TokenService(JwtTokenGeneratorPort tokenGenerator, JwtTokenKeyProviderPort keyProvider) {
        this.tokenGenerator = tokenGenerator;
        this.keyProvider = keyProvider;
    }

    public String generateToken(String agentId, String secretBase64, String teamId, List<String> actions) {
        log.info("Generating token for agent {} for team {} with actions {}", agentId, teamId, actions);

        Key expectedKey = keyProvider.resolveKey(agentId);
        if (!(expectedKey instanceof SecretKey secretKey)) {
            throw new IllegalArgumentException("Expected HMAC SecretKey for keyId=" + agentId +
                    ", but got: " + expectedKey.getClass().getSimpleName());
        }

        byte[] expected = secretKey.getEncoded();
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
                "monify",
                List.of(getClass().getPackageName().split("\\.token")[0])
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
