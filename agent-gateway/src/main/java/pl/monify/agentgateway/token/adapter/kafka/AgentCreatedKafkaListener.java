package pl.monify.agentgateway.token.adapter.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import pl.monify.agentgateway.token.adapter.mongo.MongoAgentKeyDocument;
import pl.monify.agentgateway.token.adapter.mongo.MongoAgentKeyRepository;
import pl.monify.agentgateway.token.domain.event.AgentCreatedEvent;

public class AgentCreatedKafkaListener {

    private static final Logger log = LoggerFactory.getLogger(AgentCreatedKafkaListener.class);
    private final MongoAgentKeyRepository repository;

    public AgentCreatedKafkaListener(MongoAgentKeyRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(
            topics = "${monify.kafka.agent-created-topic}",
            groupId = "${monify.kafka.group-id}",
            containerFactory = "kafkaAgentCreatedEventListenerContainerFactory"
    )
    public void handle(ConsumerRecord<String, AgentCreatedEvent> record) {
        log.info("[Kafka] Received message from agent {}", record.value().agentId());
        AgentCreatedEvent event = record.value();
        log.info("[Kafka] Agent {} created, secret: {}, name: {}", event.agentId(), event.secret(), event.name());
        repository.save(new MongoAgentKeyDocument(event.agentId(), event.secret(), event.name()));
    }
}
