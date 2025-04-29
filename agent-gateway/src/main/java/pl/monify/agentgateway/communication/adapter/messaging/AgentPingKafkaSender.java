package pl.monify.agentgateway.communication.adapter.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import pl.monify.agentgateway.communication.domain.event.AgentPingReceivedEvent;
import pl.monify.agentgateway.communication.domain.port.out.AgentPingReceivedEventPublisherPort;

public class AgentPingKafkaSender implements AgentPingReceivedEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(AgentPingKafkaSender.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public AgentPingKafkaSender(KafkaTemplate<String, Object> kafkaTemplate, String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void publish(AgentPingReceivedEvent event) {
        log.debug("[Kafka] Sending AgentPing for session={} to topic={}", event.sessionId(), topic);
        kafkaTemplate.send(topic, event);
    }
}
