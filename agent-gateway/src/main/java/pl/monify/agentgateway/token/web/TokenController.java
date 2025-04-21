package pl.monify.agentgateway.token.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.monify.agentgateway.token.application.TokenService;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class TokenController {

    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/token")
    public ResponseEntity<TokenResponse> generateToken(@RequestBody TokenRequest request) {
        String token = tokenService.generateToken(request.agentId(), request.secret(), request.teamId(), request.actions());
        return ResponseEntity.ok(new TokenResponse(token));
    }

    public record TokenRequest(String agentId, String secret, String teamId, List<String> actions) {}
    public record TokenResponse(String token) {}
}
