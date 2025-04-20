package pl.monify.agentgateway.communication.adapter.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import pl.monify.agentgateway.communication.domain.port.out.ActionResultSenderPort;
import pl.monify.agentgateway.communication.exception.KafkaException;

import java.util.Map;

public class ActionResultKafkaSender implements ActionResultSenderPort {

    private static final Logger log = LoggerFactory.getLogger(ActionResultKafkaSender.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public ActionResultKafkaSender(KafkaTemplate<String, Object> kafkaTemplate, String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void send(Map<String, Object> result) {
        try {
            String key = (String) result.get("actionInstanceId");
            kafkaTemplate.send(topic, key, result);
            log.info("[Kafka] Sent action result for {}", key);
        } catch (Exception e) {
            throw new KafkaException("Failed to send action result to Kafka", e);
        }
    }
}
