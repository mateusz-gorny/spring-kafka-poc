package pl.monify.agentgateway.communication.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import pl.monify.agentgateway.communication.domain.model.AgentSession;
import pl.monify.agentgateway.communication.domain.model.JwtAgentClaims;
import pl.monify.agentgateway.communication.domain.port.out.ConnectionRateLimiterPort;
import pl.monify.agentgateway.communication.domain.port.out.JwtTokenParserPort;
import pl.monify.agentgateway.communication.domain.port.out.RegisterAgentSessionPort;
import pl.monify.agentgateway.communication.domain.port.out.UnregisterAgentSessionPort;

public class AgentWebSocketHandler implements WebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(AgentWebSocketHandler.class);

    private final JwtTokenParserPort jwtTokenParser;
    private final RegisterAgentSessionPort registerAgent;
    private final UnregisterAgentSessionPort unregisterAgent;
    private final ConnectionRateLimiterPort connectionRateLimiterPort;
    private final AgentMessageDispatcher dispatcher;

    public AgentWebSocketHandler(
            JwtTokenParserPort jwtTokenParser,
            RegisterAgentSessionPort registerAgent,
            UnregisterAgentSessionPort unregisterAgent,
            ConnectionRateLimiterPort connectionRateLimiterPort,
            AgentMessageDispatcher dispatcher
    ) {
        this.jwtTokenParser = jwtTokenParser;
        this.registerAgent = registerAgent;
        this.unregisterAgent = unregisterAgent;
        this.connectionRateLimiterPort = connectionRateLimiterPort;
        this.dispatcher = dispatcher;
    }

    @Override
    public reactor.core.publisher.Mono<Void> handle(WebSocketSession session) {
        String header = session.getHandshakeInfo().getHeaders().getFirst("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return session.close(new CloseStatus(4001, "Missing or invalid Authorization header"));
        }

        String token = header.substring("Bearer ".length());
        JwtAgentClaims claims;

        try {
            claims = jwtTokenParser.parse(token);
        } catch (IllegalArgumentException e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            return session.close(new CloseStatus(4002, "Invalid JWT token"));
        } catch (Exception e) {
            log.error("Unexpected error during JWT validation", e);
            return session.close(new CloseStatus(4003, "JWT parsing error"));
        }

        if (!connectionRateLimiterPort.allowConnection()) {
            log.warn("Connection rejected: rate limit exceeded");
            return session.close(new CloseStatus(1013, "Try again later"));
        }

        String agentId = claims.agentId();
        MDC.put("agent", agentId);
        AgentSession agentSession = new AgentSession(session, claims.teamId(), claims.action());

        registerAgent.register(agentId, agentSession);

        return session.receive()
                .doFinally(signalType -> {
                    unregisterAgent.unregister(agentId);
                    MDC.clear();
                })
                .flatMap(msg -> dispatcher.dispatch(agentSession, msg.getPayloadAsText()))
                .then();
    }
}
