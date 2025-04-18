package pl.monify.agentgateway.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

public class AgentSession {

    private static final Logger log = LoggerFactory.getLogger(AgentSession.class);

    private final String agentId;
    private final String teamId;
    private final WebSocketSession session;
    private final String correlationId;

    public AgentSession(String agentId, String teamId, WebSocketSession session) {
        this(agentId, teamId, session, null);
    }

    public AgentSession(String agentId, String teamId, WebSocketSession session, String correlationId) {
        this.agentId = agentId;
        this.teamId = teamId;
        this.session = session;
        this.correlationId = correlationId != null ? correlationId : java.util.UUID.randomUUID().toString();
    }

    public String getId() {
        return agentId;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public Mono<Void> sendText(String text) {
        try {
            MDC.put("correlationId", correlationId);
            MDC.put("agentId", agentId);
            MDC.put("teamId", teamId);
            log.debug("[WS] Sending to {}: {}", agentId, text);
            return session.send(Mono.just(session.textMessage(text)));
        } finally {
            MDC.clear();
        }
    }
}
