package pl.monify.agentgateway.token.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.monify.agentgateway.token.application.TokenService;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class TokenController {

    private static final Logger log = LoggerFactory.getLogger(TokenController.class);
    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/token")
    public ResponseEntity<TokenResponse> generateToken(@RequestBody TokenRequest request) {
        log.info("Received token request for agent {} for team {}", request.agentId(), request.teamId());
        return ResponseEntity.ok(new TokenResponse(
                tokenService.generateToken(request.agentId(), request.secret(), request.teamId(), request.actions())
        ));
    }

    public record TokenRequest(String agentId, String secret, String teamId, List<String> actions) {}
    public record TokenResponse(String token) {}
}
