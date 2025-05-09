package pl.monify.agentgateway.communication.web.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import pl.monify.agentgateway.agentdelivery.domain.model.ActionType;
import pl.monify.agentgateway.communication.adapter.registry.RegisterAgentMessage;
import pl.monify.agentgateway.communication.domain.model.AgentRegisterModel;
import pl.monify.agentgateway.communication.domain.model.AgentSession;
import pl.monify.agentgateway.communication.domain.port.in.RegisterAgentUseCase;
import pl.monify.agentgateway.communication.exception.KafkaException;
import pl.monify.agentgateway.communication.web.AgentMessageHandler;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class RegisterHandler implements AgentMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(RegisterHandler.class);

    private final ObjectMapper objectMapper;
    private final RegisterAgentUseCase registerAgent;

    public RegisterHandler(ObjectMapper objectMapper,
                           RegisterAgentUseCase registerAgent) {
        this.objectMapper = objectMapper;
        this.registerAgent = registerAgent;
    }

    @Override
    public String type() {
        return "register";
    }

    @Override
    public Mono<Void> handle(String json, AgentSession session) {
        log.info("[WS] Received register message from agent {}", session.id());
        MDC.put("sessionId", session.id());
        MDC.put("teamId", session.teamId());

        try {
            RegisterAgentMessage msg = objectMapper.readValue(json, RegisterAgentMessage.class);

            if (msg.action() == null || msg.action().isBlank()) {
                log.error("[WS] Invalid register message payload");
                return session.sendText("{\"type\":\"error\",\"payload\":{\"message\":\"invalid payload\"}}");
            }

            log.info("[WS] Registering agent {} for team {}", msg.action(), session.teamId());

            Duration ttl = null;
            if (msg.ttl() != null) {
                try {
                    ttl = Duration.parse(msg.ttl());
                } catch (DateTimeParseException e) {
                    log.warn("[WS] Invalid TTL format from agent '{}': {}", session.id(), msg.ttl());
                }
            }

            registerAgent.register(new AgentRegisterModel(
                    msg.agentId(),
                    msg.teamId(),
                    msg.action(),
                    session,
                    msg.inputSchema(),
                    msg.outputSchema(),
                    Optional.ofNullable(msg.actionType())
                            .map(s -> ActionType.valueOf(s.toUpperCase()))
                            .orElse(ActionType.ACTION),
                    ttl
            ));

            log.info("[WS] Agent registered for team {}", session.teamId());
            return session.sendText("{\"type\":\"registered\"}");

        } catch (KafkaException e) {
            log.error("[WS] Kafka failure while registering agent", e);
            return session.sendText("{\"type\":\"error\",\"payload\":{\"message\":\"internal Kafka error\"}}");

        } catch (Exception e) {
            log.error("[WS] Error during register handling", e);
            return session.sendText("{\"type\":\"error\",\"payload\":{\"message\":\"unexpected error\"}}");

        } finally {
            MDC.clear();
        }
    }
}
