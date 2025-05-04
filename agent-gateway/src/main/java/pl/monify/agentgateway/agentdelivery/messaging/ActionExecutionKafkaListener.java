package pl.monify.agentgateway.agentdelivery.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import pl.monify.agentgateway.agentdelivery.adapter.mongo.ActionExecutionRequestDocument;
import pl.monify.agentgateway.agentdelivery.adapter.mongo.ActionExecutionRequestMongoRepository;
import pl.monify.agentgateway.agentdelivery.domain.application.AgentDispatcher;
import pl.monify.agentgateway.agentdelivery.domain.model.ActionExecutionRequestMessage;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class ActionExecutionKafkaListener {

    private static final Logger log = LoggerFactory.getLogger(ActionExecutionKafkaListener.class);
    private final AgentDispatcher dispatcher;
    private final ActionExecutionRequestMongoRepository repository;
    private final ObjectMapper mapper;

    public ActionExecutionKafkaListener(
            AgentDispatcher dispatcher,
            ActionExecutionRequestMongoRepository repository,
            ObjectMapper mapper
    ) {
        this.dispatcher = dispatcher;
        this.repository = repository;
        this.mapper = mapper;
    }

    @KafkaListener(
            topics = "${monify.kafka.agent-execution-topic}",
            groupId = "${monify.kafka.group-id}",
            containerFactory = "kafkaActionExecutionListenerContainerFactory"
    )
    public void handle(@Payload ActionExecutionRequestMessage requestMessage) {
        log.info("[Kafka] Received message from agent {}", requestMessage.correlationId());

        try {
            String raw = mapper.writeValueAsString(requestMessage);
            var doc = new ActionExecutionRequestDocument(
                    null,
                    requestMessage.action(),
                    requestMessage.teamId(),
                    raw,
                    Instant.now().plus(365, ChronoUnit.DAYS)
            );
            repository.save(doc);
        } catch (Exception e) {
            log.error("[Kafka] Failed to persist request: {}", requestMessage.correlationId(), e);
        }

        dispatcher.dispatch(requestMessage);
    }
}
