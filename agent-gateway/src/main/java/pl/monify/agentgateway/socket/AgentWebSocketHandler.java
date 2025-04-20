package pl.monify.agentgateway.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import pl.monify.agentgateway.handler.AgentMessageHandler;
import pl.monify.agentgateway.messaging.AgentMessage;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class AgentWebSocketHandler implements WebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(AgentWebSocketHandler.class);

    private final JwtParser jwtParser;
    private final ObjectMapper objectMapper;
    private final AgentRegistry agentRegistry;
    private final Map<String, AgentMessageHandler> handlers;
    private final RateLimiterService rateLimiter;

    public AgentWebSocketHandler(
            JwtParser jwtParser,
            ObjectMapper objectMapper,
            AgentRegistry agentRegistry,
            List<AgentMessageHandler> handlerList,
            RateLimiterService rateLimiter
    ) {
        this.jwtParser = jwtParser;
        this.objectMapper = objectMapper;
        this.agentRegistry = agentRegistry;
        this.rateLimiter = rateLimiter;
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(AgentMessageHandler::type, h -> h));
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        // Generate a correlation ID for this connection
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);

        log.info("[WS] New agent connection attempt");

        // Apply rate limiting for connections
        if (!rateLimiter.allowConnection()) {
            log.warn("[WS] Connection rate limit exceeded, rejecting connection");
            return session.close(new CloseStatus(4029, "Rate limit exceeded"));
        }

        String token = Optional.of(session.getHandshakeInfo().getUri())
                .map(URI::getQuery)
                .filter(query -> query.startsWith("token="))
                .map(query -> query.substring("token=".length()))
                .orElse(null);

        if (token == null) {
            log.error("[WS] Missing token");
            return session.close(new CloseStatus(4000, "Missing token"));
        }

        Claims claims;
        try {
            claims = jwtParser.parseSignedClaims(token).getPayload();
        } catch (Exception e) {
            log.error("[WS] Failed to parse JWT token", e);
            return session.close(new CloseStatus(4001, "Invalid JWT"));
        }

        String agentId = claims.getSubject();
        String teamId = claims.get("team_id", String.class);
        Boolean isAgent = claims.get("agent", Boolean.class);

        // Update MDC with agent and team IDs
        MDC.put("agentId", agentId);
        MDC.put("teamId", teamId);

        log.info("[WS] Agent {} connected (team={}) - {}", agentId, teamId, isAgent);
        if (agentId == null || teamId == null || Boolean.FALSE.equals(isAgent)) {
            log.error("[WS] Invalid claims: agentId={}, teamId={}, agent={}", agentId, teamId, isAgent);
            return session.close(new CloseStatus(4002, "Missing or invalid claims"));
        }

        // Check if token is expired
        Date expiration = claims.getExpiration();
        if (expiration != null && expiration.before(new Date())) {
            log.error("[WS] Token is expired for agent {}", agentId);
            return session.close(new CloseStatus(4003, "Token expired"));
        }

        AgentSession agentSession = new AgentSession(agentId, teamId, session, correlationId);
        agentRegistry.register(agentId, agentSession);

        log.info("[WS] Agent {} registered (team={})", agentId, teamId);

        return session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(msg -> handleMessage(msg, agentSession))
                .then()
                .doFinally(signal -> {
                    agentRegistry.unregister(agentId);
                    MDC.clear();
                });
    }

    private Mono<Void> handleMessage(String json, AgentSession session) {
        try {
            // Set up MDC context
            MDC.put("correlationId", session.getCorrelationId());
            MDC.put("agentId", session.getId());
            MDC.put("teamId", session.getTeamId());

            // Apply rate limiting for messages
            if (!rateLimiter.allowMessage(session.getId())) {
                log.warn("[WS] Message rate limit exceeded for agent {}", session.getId());
                return session.sendText("{\"type\":\"error\",\"payload\":{\"message\":\"Rate limit exceeded\"}}");
            }

            AgentMessage msg = objectMapper.readValue(json, AgentMessage.class);
            log.info("[WS] Agent {} received message: {}", session.getId(), msg.type());

            AgentMessageHandler handler = handlers.get(msg.type());
            if (handler == null) {
                log.warn("[WS] Unsupported message type: {}", msg.type());
                return session.sendText("{\"type\":\"error\",\"payload\":{\"message\":\"Unsupported type\"}}");
            }

            return handler.handle(json, session);
        } catch (Exception e) {
            log.warn("[WS] Failed to parse agent message", e);
            return session.sendText("{\"type\":\"error\",\"payload\":{\"message\":\"Invalid format\"}}");
        } finally {
            MDC.clear();
        }
    }
}
