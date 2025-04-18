package pl.monify.agentgateway.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import pl.monify.agentgateway.config.KafkaProperties;
import pl.monify.agentgateway.exception.KafkaException;
import pl.monify.agentgateway.exception.MessageHandlingException;
import pl.monify.agentgateway.messaging.AgentRegisteredMessage;
import pl.monify.agentgateway.messaging.RegisterAgentMessage;
import pl.monify.agentgateway.messaging.kafka.KafkaProducerService;
import pl.monify.agentgateway.socket.ActionRegistry;
import pl.monify.agentgateway.socket.AgentSession;
import reactor.core.publisher.Mono;

@Component
public class RegisterMessageHandler implements AgentMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(RegisterMessageHandler.class);

    private final ObjectMapper objectMapper;
    private final KafkaProducerService kafkaProducer;
    private final ActionRegistry actionRegistry;
    private final KafkaProperties kafkaProperties;

    public RegisterMessageHandler(
            ObjectMapper objectMapper, 
            KafkaProducerService kafkaProducer, 
            ActionRegistry actionRegistry,
            KafkaProperties kafkaProperties) {
        this.objectMapper = objectMapper;
        this.kafkaProducer = kafkaProducer;
        this.actionRegistry = actionRegistry;
        this.kafkaProperties = kafkaProperties;
    }

    @Override
    public String type() {
        return "register";
    }

    @Override
    public Mono<Void> handle(String json, AgentSession session) {
        try {
            MDC.put("correlationId", session.getCorrelationId());
            MDC.put("agentId", session.getId());
            MDC.put("teamId", session.getTeamId());

            log.info("[WS] Received register message from agent {}", session.getId());

            // Parse the message
            RegisterAgentMessage msg;
            try {
                msg = objectMapper.readValue(json, RegisterAgentMessage.class);
            } catch (JsonProcessingException e) {
                log.error("[WS] Failed to parse register message JSON", e);
                throw new MessageHandlingException("register", "Invalid JSON format", e);
            }

            // Validate the message
            if (msg.payload() == null) {
                log.error("[WS] Missing payload in register message");
                throw new MessageHandlingException("register", "Missing payload");
            }

            if (msg.payload().action() == null || msg.payload().action().isBlank()) {
                log.error("[WS] Missing action in register message payload");
                throw new MessageHandlingException("register", "Missing action in payload");
            }

            // Register the action
            actionRegistry.register(
                    session.getTeamId(),
                    msg.payload().action(),
                    session,
                    msg.payload().inputSchema(),
                    msg.payload().outputSchema()
            );

            log.info("[WS] Registered action '{}' for agent {}", msg.payload().action(), session.getId());

            // Create and send the registration event
            AgentRegisteredMessage event = new AgentRegisteredMessage(
                    msg.payload().action(),
                    session.getId(),
                    session.getTeamId(),
                    msg.payload().inputSchema(),
                    msg.payload().outputSchema()
            );

            try {
                kafkaProducer.send(kafkaProperties.agentRegistrationTopic(), event);
                log.info("[WS] Sent agent registration event for action '{}' to Kafka", msg.payload().action());
            } catch (KafkaException e) {
                log.error("[WS] Failed to send agent registration event to Kafka", e);
                return session.sendText("{\"type\":\"error\",\"payload\":{\"message\":\"Failed to register action\"}}");
            }

            return session.sendText("{\"type\":\"registered\"}");
        } catch (MessageHandlingException e) {
            log.error("[WS] Message handling error", e);
            return session.sendText("{\"type\":\"error\",\"payload\":{\"message\":\"" + e.getMessage() + "\"}}");
        } catch (Exception e) {
            log.error("[WS] Unexpected error handling register message", e);
            return session.sendText("{\"type\":\"error\",\"payload\":{\"message\":\"Internal server error\"}}");
        } finally {
            MDC.clear();
        }
    }
}
