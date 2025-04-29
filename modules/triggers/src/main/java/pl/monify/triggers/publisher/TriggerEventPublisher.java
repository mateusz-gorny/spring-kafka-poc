package pl.monify.triggers.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TriggerEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(TriggerEventPublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final String TOPIC = "workflow.trigger.event";

    public TriggerEventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            ObjectMapper objectMapper
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publish(Map<String, Object> event) {
        try {
            kafkaTemplate.send(TOPIC, event);
        } catch (Exception e) {
            log.error("Serialization error while publishing event: {}", event, e);
            throw new RuntimeException("Serialization error while publishing event: " + event, e);
        }
    }
}

