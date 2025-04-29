package pl.monify.agentstatus.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.monify.agentstatus.adapter.mongo.AgentPingDocument;
import pl.monify.agentstatus.adapter.mongo.AgentPingRepository;
import pl.monify.agentstatus.adapter.mongo.AgentSecretDocument;
import pl.monify.agentstatus.adapter.mongo.AgentSecretRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@RestController
@RequestMapping("/api/agents")
public class AgentQueryController {

    private final AgentSecretRepository secretRepository;
    private final AgentPingRepository pingRepository;

    public AgentQueryController(AgentSecretRepository secretRepository, AgentPingRepository pingRepository) {
        this.secretRepository = secretRepository;
        this.pingRepository = pingRepository;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('AGENT_USER')")
    public List<Map<String, Object>> list() {
        return secretRepository.findAll().stream().map(agent -> {
            Optional<AgentPingDocument> lastPing = findLastPing(agent.getId());
            boolean isActive = isActive(lastPing);

            Map<String, Object> result = new HashMap<>();
            result.put("id", agent.getId());
            result.put("name", agent.getName());
            result.put("isActive", isActive);
            lastPing.ifPresent(agentPingDocument -> result.put("lastPingAt", agentPingDocument.getTimestamp()));
            return result;
        }).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('AGENT_USER')")
    public ResponseEntity<Map<String, Object>> getDetails(@PathVariable("id") String id) {
        Optional<AgentSecretDocument> agentOpt = secretRepository.findById(id);
        if (agentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        AgentSecretDocument agent = agentOpt.get();
        Optional<AgentPingDocument> lastPing = findLastPing(id);
        boolean isActive = isActive(lastPing);

        Map<String, Object> result = new HashMap<>();
        result.put("id", agent.getId());
        result.put("name", agent.getName());
        result.put("isActive", isActive);
        result.put("secret", "***************");
        if (lastPing.isPresent()) {
            result.put("lastPingAt", lastPing.get().getTimestamp());
            result.put("hostStats", lastPing.get().getHostStats());
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/ping-history")
    @PreAuthorize("hasAuthority('AGENT_USER')")
    public List<Map<String, Object>> pingHistory(@PathVariable("id") String id) {
        return pingRepository.findTop300ByAgentIdOrderByTimestampDesc(id).stream()
                .map(ping -> {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("timestamp", ping.getTimestamp());
                    entry.put("hostStats", ping.getHostStats());
                    return entry;
                })
                .toList();
    }

    private Optional<AgentPingDocument> findLastPing(String agentId) {
        return pingRepository.findTopByAgentIdOrderByTimestampDesc(agentId);
    }

    private boolean isActive(Optional<AgentPingDocument> ping) {
        return ping
                .map(p -> Duration.between(p.getTimestamp(), Instant.now()).getSeconds() <= 60)
                .orElse(false);
    }
}
