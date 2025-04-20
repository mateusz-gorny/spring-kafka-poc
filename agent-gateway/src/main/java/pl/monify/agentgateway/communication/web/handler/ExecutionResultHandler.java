package pl.monify.agentgateway.communication.web.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import pl.monify.agentgateway.communication.adapter.messaging.ActionExecutionResultMessage;
import pl.monify.agentgateway.communication.domain.model.AgentSession;
import pl.monify.agentgateway.communication.domain.port.in.HandleActionExecutionResultUseCase;
import pl.monify.agentgateway.communication.domain.port.out.MessageRateLimiterPort;
import pl.monify.agentgateway.communication.exception.KafkaException;
import pl.monify.agentgateway.communication.web.AgentMessageHandler;
import reactor.core.publisher.Mono;

public class ExecutionResultHandler implements AgentMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(ExecutionResultHandler.class);

    private final ObjectMapper objectMapper;
    private final MessageRateLimiterPort messageRateLimiterPort;
    private final HandleActionExecutionResultUseCase resultUseCase;

    public ExecutionResultHandler(ObjectMapper objectMapper,
                                  MessageRateLimiterPort messageRateLimiterPort,
                                  HandleActionExecutionResultUseCase resultUseCase) {
        this.objectMapper = objectMapper;
        this.messageRateLimiterPort = messageRateLimiterPort;
        this.resultUseCase = resultUseCase;
    }

    @Override
    public String type() {
        return "ActionExecutionResult";
    }

    @Override
    public Mono<Void> handle(String json, AgentSession session) {
        MDC.put("agentId", session.id());
        MDC.put("teamId", session.teamId());

        if (messageRateLimiterPort.isRateLimited(session.id())) {
            log.warn("[WS] Rate limit exceeded for result from agent {}", session.id());
            MDC.clear();
            return session.sendText("{\"type\":\"error\",\"payload\":{\"message\":\"rate limit exceeded\"}}");
        }

        try {
            ActionExecutionResultMessage result = objectMapper.readValue(json, ActionExecutionResultMessage.class);

            resultUseCase.handle(
                    result.correlationId(),
                    result.payload().status(),
                    result.payload().output(),
                    result.payload().logs()
            );

            return Mono.empty();

        } catch (KafkaException e) {
            log.error("[WS] Kafka failure while processing result", e);
            return session.sendText("{\"type\":\"error\",\"payload\":{\"message\":\"internal Kafka error\"}}");

        } catch (Exception e) {
            log.error("[WS] Error during result handling", e);
            return session.sendText("{\"type\":\"error\",\"payload\":{\"message\":\"unexpected error\"}}");

        } finally {
            MDC.clear();
        }
    }
}
