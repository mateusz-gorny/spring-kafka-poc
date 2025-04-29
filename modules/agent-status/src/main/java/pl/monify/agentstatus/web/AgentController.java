package pl.monify.agentstatus.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.monify.agentstatus.adapter.mongo.AgentSecretDocument;
import pl.monify.agentstatus.adapter.mongo.AgentSecretRepository;
import pl.monify.agentstatus.application.CreateAgentService;

import java.util.Map;

@RestController
@RequestMapping("/api/agents")
public class AgentController {

    private final CreateAgentService createAgentService;
    private final AgentSecretRepository agentSecretRepository;
    private final AuthenticationManager authenticationManager;

    public AgentController(CreateAgentService createAgentService,
                           AgentSecretRepository agentSecretRepository,
                           AuthenticationManager authenticationManager) {
        this.createAgentService = createAgentService;
        this.agentSecretRepository = agentSecretRepository;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('AGENT_ADMIN')")
    public ResponseEntity<Map<String, String>> create(@RequestBody Map<String, String> body) {
        String name = body.getOrDefault("name", "unnamed");
        AgentSecretDocument agent = createAgentService.create(name);
        return ResponseEntity.ok(Map.of(
                "id", agent.getId(),
                "secret", agent.getSecret()
        ));
    }

    @PostMapping("/{id}/reveal-secret")
    @PreAuthorize("hasAuthority('AGENT_ADMIN')")
    public ResponseEntity<Map<String, String>> revealSecret(@PathVariable("id") String id, @RequestBody SecretCredentials secretRevalRequest) {
        if (secretRevalRequest.credentials() == null) {
            return ResponseEntity.badRequest().build();
        }

        String username = secretRevalRequest.credentials().username();
        String password = secretRevalRequest.credentials().password();

        if (username == null || password == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Authentication auth = new UsernamePasswordAuthenticationToken(username, password);
            authenticationManager.authenticate(auth);
        } catch (Exception e) {
            return ResponseEntity.status(403).build();
        }

        return agentSecretRepository.findById(id)
                .map(agent -> ResponseEntity.ok(Map.of("secret", agent.getSecret())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public record SecretCredentials(SecretRevalRequest credentials) {
    }

    public record SecretRevalRequest(String username, String password) {
    }
}
