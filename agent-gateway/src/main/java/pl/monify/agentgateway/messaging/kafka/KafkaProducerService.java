package pl.monify.agentgateway.messaging.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import pl.monify.agentgateway.config.CircuitBreakerConfig.CircuitBreakerSettingsProvider;
import pl.monify.agentgateway.exception.KafkaException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class KafkaProducerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);
    private static final String CIRCUIT_NAME = "kafka-producer";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final CircuitBreakerService circuitBreaker;
    private final CircuitBreakerSettingsProvider circuitBreakerSettings;

    public KafkaProducerService(
            KafkaTemplate<String, Object> kafkaTemplate,
            CircuitBreakerService circuitBreaker,
            CircuitBreakerSettingsProvider circuitBreakerSettings) {
        this.kafkaTemplate = kafkaTemplate;
        this.circuitBreaker = circuitBreaker;
        this.circuitBreakerSettings = circuitBreakerSettings;
    }

    /**
     * Sends a message to a Kafka topic with circuit breaker protection.
     *
     * @param topic   the Kafka topic
     * @param message the message to send
     * @throws KafkaException if the message cannot be sent
     */
    public void send(String topic, Object message) {
        String correlationId = MDC.get("correlationId");
        if (correlationId == null) {
            correlationId = java.util.UUID.randomUUID().toString();
            MDC.put("correlationId", correlationId);
        }

        log.debug("Sending message to Kafka topic {}: {}", topic, message);

        if (!circuitBreaker.isAllowed(CIRCUIT_NAME)) {
            log.error("Circuit breaker open, not sending message to topic {}", topic);
            throw new KafkaException(topic, "send", "Circuit breaker open");
        }

        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, message);
            int timeoutSeconds = circuitBreakerSettings.getTimeoutSeconds(CIRCUIT_NAME);
            future.get(timeoutSeconds, TimeUnit.SECONDS);
            circuitBreaker.recordSuccess(CIRCUIT_NAME);
            log.debug("Successfully sent message to Kafka topic {}", topic);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            circuitBreaker.recordFailure(CIRCUIT_NAME);
            log.error("Interrupted while sending message to Kafka topic {}", topic, e);
            throw new KafkaException(topic, "send", "Interrupted while sending message", e);
        } catch (ExecutionException e) {
            circuitBreaker.recordFailure(CIRCUIT_NAME);
            log.error("Error sending message to Kafka topic {}", topic, e.getCause());
            throw new KafkaException(topic, "send", "Error sending message", e.getCause());
        } catch (TimeoutException e) {
            circuitBreaker.recordFailure(CIRCUIT_NAME);
            log.error("Timeout sending message to Kafka topic {}", topic);
            throw new KafkaException(topic, "send", "Timeout sending message", e);
        } finally {
            MDC.remove("correlationId");
        }
    }
}
