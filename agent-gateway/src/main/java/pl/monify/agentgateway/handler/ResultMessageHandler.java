package pl.monify.agentgateway.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import pl.monify.agentgateway.config.KafkaProperties;
import pl.monify.agentgateway.exception.KafkaException;
import pl.monify.agentgateway.exception.MessageHandlingException;
import pl.monify.agentgateway.messaging.kafka.KafkaProducerService;
import pl.monify.agentgateway.socket.AgentSession;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class ResultMessageHandler implements AgentMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(ResultMessageHandler.class);

    private final ObjectMapper objectMapper;
    private final KafkaProducerService kafkaProducer;
    private final KafkaProperties kafkaProperties;

    public ResultMessageHandler(ObjectMapper objectMapper, KafkaProducerService kafkaProducer, KafkaProperties kafkaProperties) {
        this.objectMapper = objectMapper;
        this.kafkaProducer = kafkaProducer;
        this.kafkaProperties = kafkaProperties;
    }

    @Override
    public String type() {
        return "ActionExecutionResult";
    }

    @Override
    public Mono<Void> handle(String json, AgentSession session) {
        try {
            MDC.put("correlationId", session.getCorrelationId());
            MDC.put("agentId", session.getId());
            MDC.put("teamId", session.getTeamId());

            log.info("[WS] Received ActionExecutionResult from agent {}", session.getId());

            // Parse the message
            Map<String, Object> raw;
            try {
                raw = objectMapper.readValue(json, new TypeReference<>() {});
            } catch (JsonProcessingException e) {
                log.error("[WS] Failed to parse ActionExecutionResult JSON", e);
                throw new MessageHandlingException("ActionExecutionResult", "Invalid JSON format", e);
            }

            // Extract correlation ID
            String correlationId = (String) raw.get("correlationId");
            if (correlationId == null) {
                log.error("[WS] Missing correlationId in ActionExecutionResult");
                throw new MessageHandlingException("ActionExecutionResult", "Missing correlationId");
            }

            // Add correlation ID to MDC
            MDC.put("messageCorrelationId", correlationId);

            log.debug("[WS] Raw message: {}", raw);

            // Extract payload
            @SuppressWarnings("unchecked")
            Map<String, Object> payload = (Map<String, Object>) raw.get("payload");
            if (payload == null) {
                log.error("[WS] Missing payload in ActionExecutionResult");
                throw new MessageHandlingException("ActionExecutionResult", "Missing payload");
            }

            // Extract fields from payload
            String status = (String) payload.get("status");
            if (status == null) {
                log.error("[WS] Missing status in ActionExecutionResult payload");
                throw new MessageHandlingException("ActionExecutionResult", "Missing status in payload");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> output = (Map<String, Object>) payload.get("output");
            if (output == null) {
                log.warn("[WS] Missing output in ActionExecutionResult payload, using empty map");
                output = Collections.emptyMap();
            }

            @SuppressWarnings("unchecked")
            List<String> logs = (List<String>) payload.get("logs");

            boolean success = "SUCCESS".equalsIgnoreCase(status);
            String logText = logs != null ? String.join("\n", logs) : "";

            var result = Map.of(
                    "actionInstanceId", correlationId,
                    "success", success,
                    "log", logText,
                    "output", output
            );

            try {
                kafkaProducer.send(kafkaProperties.actionResultTopic(), result);
                log.info("[WS] Forwarded result for actionInstanceId={} to workflow", correlationId);
            } catch (KafkaException e) {
                log.error("[WS] Failed to send ActionExecutionResult to Kafka", e);
                return session.sendText("{\"type\":\"error\",\"payload\":{\"message\":\"Failed to process result\"}}");
            }

            return Mono.empty();
        } catch (MessageHandlingException e) {
            log.error("[WS] Message handling error", e);
            return session.sendText("{\"type\":\"error\",\"payload\":{\"message\":\"" + e.getMessage() + "\"}}");
        } catch (Exception e) {
            log.error("[WS] Unexpected error handling ActionExecutionResult", e);
            return session.sendText("{\"type\":\"error\",\"payload\":{\"message\":\"Internal server error\"}}");
        } finally {
            MDC.clear();
        }
    }
}
