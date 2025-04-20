package pl.monify.agentgateway.communication.web.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import pl.monify.agentgateway.communication.domain.model.AgentSession;
import pl.monify.agentgateway.communication.domain.port.in.PingAgentUseCase;
import pl.monify.agentgateway.communication.domain.port.out.MessageRateLimiterPort;
import pl.monify.agentgateway.communication.web.AgentMessageHandler;
import reactor.core.publisher.Mono;

public class PingHandler implements AgentMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(PingHandler.class);

    private final MessageRateLimiterPort messageRateLimiterPort;
    private final PingAgentUseCase pingAgentUseCase;

    public PingHandler(MessageRateLimiterPort messageRateLimiterPort, PingAgentUseCase pingAgentUseCase) {
        this.messageRateLimiterPort = messageRateLimiterPort;
        this.pingAgentUseCase = pingAgentUseCase;
    }

    @Override
    public String type() {
        return "ping";
    }

    @Override
    public Mono<Void> handle(String json, AgentSession session) {
        MDC.put("agentId", session.id());
        MDC.put("teamId", session.teamId());

        if (messageRateLimiterPort.isRateLimited(session.id())) {
            log.warn("[WS] Rate limit exceeded for ping from agent {}", session.id());
            MDC.clear();
            return session.sendText("{\"type\":\"error\",\"payload\":{\"message\":\"rate limit exceeded\"}}");
        }

        log.debug("[WS] Received ping from agent {}", session.id());
        pingAgentUseCase.pong(session);
        MDC.clear();
        return Mono.empty();
    }
}
