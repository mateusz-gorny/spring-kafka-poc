package pl.monify.agentstatus.adapter.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import pl.monify.agentstatus.domain.event.AgentCreatedEvent;
import pl.monify.agentstatus.domain.port.out.AgentCreatedEventPublisherPort;

public class AgentCreatedKafkaPublisher implements AgentCreatedEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(AgentCreatedKafkaPublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public AgentCreatedKafkaPublisher(KafkaTemplate<String, Object> kafkaTemplate, String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void publish(AgentCreatedEvent event) {
        log.info("Publishing AgentCreatedEvent for agent {}", event.agentId());
        kafkaTemplate.send(topic, event.agentId(), event);
    }
}
