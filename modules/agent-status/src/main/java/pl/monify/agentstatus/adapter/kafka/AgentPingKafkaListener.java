package pl.monify.agentstatus.adapter.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import pl.monify.agentstatus.application.AgentPingHandlerService;
import pl.monify.agentstatus.domain.model.AgentPing;

public class AgentPingKafkaListener {

    private static final Logger log = LoggerFactory.getLogger(AgentPingKafkaListener.class);
    private final AgentPingHandlerService handlerService;

    public AgentPingKafkaListener(AgentPingHandlerService handlerService) {
        this.handlerService = handlerService;
    }

    @KafkaListener(
            topics = "${monify.kafka.agent-ping-topic}",
            groupId = "${monify.kafka.group-id}",
            containerFactory = "kafkaActionAgentPingListenerContainerFactory"
    )
    public void handle(ConsumerRecord<String, AgentPing> record) {
        log.info("Received ping: {}", record.value());
        handlerService.handle(record.value());
    }
}
