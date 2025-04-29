package pl.monify.agentstatus.domain.model;

import java.time.Instant;
import java.util.Map;

public record AgentPing(String agentId, String sessionId, String teamId, Instant timestamp, Map<String, Object> hostStats) {
}
