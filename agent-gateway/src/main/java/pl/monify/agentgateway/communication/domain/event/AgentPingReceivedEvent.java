package pl.monify.agentgateway.communication.domain.event;

import pl.monify.agentgateway.communication.domain.model.AgentHostStats;

import java.time.Instant;

public record AgentPingReceivedEvent(String agentId, String sessionId, String teamId, Instant timestamp, AgentHostStats hostStats) {
}
