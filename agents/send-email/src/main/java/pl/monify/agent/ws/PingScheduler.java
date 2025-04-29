package pl.monify.agent.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import pl.monify.agent.infra.metrics.AgentHostStatsProvider;
import pl.monify.agent.model.AgentPingModel;

import java.time.Instant;

public class PingScheduler {

    private static final Logger log = LoggerFactory.getLogger(PingScheduler.class);
    private final String agentId;
    private final String teamId;
    private final SessionRegistry sessionRegistry;
    private final ObjectMapper mapper;
    private final AgentHostStatsProvider hostStatsProvider;

    public PingScheduler(String agentId, String teamId, SessionRegistry sessionRegistry, ObjectMapper mapper, AgentHostStatsProvider hostStatsProvider) {
        this.sessionRegistry = sessionRegistry;
        this.mapper = mapper;
        this.hostStatsProvider = hostStatsProvider;
        this.agentId = agentId;
        this.teamId = teamId;
    }

    @Scheduled(fixedDelay = 60_000)
    public void ping() {
        log.info("[WS] Sending ping to agent {}", agentId);
        if (sessionRegistry.connected()) {
            log.info("[WS] Agent {} is connected, sending ping", agentId);
            try {
                sessionRegistry.get().send(mapper.writeValueAsString(new AgentPingModel(
                        "ping",
                        agentId,
                        teamId,
                        Instant.now(),
                        hostStatsProvider.getStats().asMap()
                )));
            } catch (Exception error) {
                log.error("[WS] Failed to send ping to agent {}", agentId, error);
            }
        }
    }
}
