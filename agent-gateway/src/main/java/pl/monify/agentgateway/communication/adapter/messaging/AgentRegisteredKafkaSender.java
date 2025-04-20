package pl.monify.agentgateway.communication.adapter.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import pl.monify.agentgateway.communication.domain.model.AgentRegisteredMessage;
import pl.monify.agentgateway.communication.domain.port.out.AgentRegisteredEventSenderPort;
import pl.monify.agentgateway.communication.exception.KafkaException;

public class AgentRegisteredKafkaSender implements AgentRegisteredEventSenderPort {

    private static final Logger log = LoggerFactory.getLogger(AgentRegisteredKafkaSender.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public AgentRegisteredKafkaSender(KafkaTemplate<String, Object> kafkaTemplate, String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void send(AgentRegisteredMessage message) {
        try {
            kafkaTemplate.send(topic, message.agentId(), message);
            log.info("[Kafka] Sent agent registration for agent {}", message.agentId());
        } catch (Exception e) {
            throw new KafkaException("Failed to send agent registration event", e);
        }
    }
}
