package pl.monify.agent.model;

import java.time.Instant;
import java.util.Map;

public record AgentPingModel(String type, String agentId, String teamId, Instant timestamp, Map<String, Object> hostStats) {
}
