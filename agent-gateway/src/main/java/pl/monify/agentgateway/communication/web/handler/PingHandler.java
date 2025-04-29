package pl.monify.agentgateway.communication.web.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import pl.monify.agentgateway.communication.domain.model.AgentPing;
import pl.monify.agentgateway.communication.domain.model.AgentSession;
import pl.monify.agentgateway.communication.domain.port.in.PingAgentUseCase;
import pl.monify.agentgateway.communication.web.AgentMessageHandler;
import reactor.core.publisher.Mono;

public class PingHandler implements AgentMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(PingHandler.class);

    private final PingAgentUseCase pingAgentUseCase;
    private final ObjectMapper mapper;

    public PingHandler(PingAgentUseCase pingAgentUseCase, ObjectMapper mapper) {
        this.pingAgentUseCase = pingAgentUseCase;
        this.mapper = mapper;
    }

    @Override
    public String type() {
        return "ping";
    }

    @Override
    public Mono<Void> handle(String json, AgentSession agentSession) throws JsonProcessingException {
        log.info("[WS] Received ping message from agent {}", agentSession.id());
        MDC.put("sessionId", agentSession.id());
        MDC.put("teamId", agentSession.teamId());

        pingAgentUseCase.pong(agentSession, mapper.readValue(json, AgentPing.class));

        MDC.clear();
        log.info("[WS] Pong processed from agent {}", agentSession.id());
        return Mono.empty();
    }
}
