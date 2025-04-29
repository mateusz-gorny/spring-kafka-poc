package pl.monify.agentgateway.agentdelivery.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import pl.monify.agentgateway.agentdelivery.domain.application.AgentDispatcher;
import pl.monify.agentgateway.agentdelivery.domain.model.ActionExecutionRequestMessage;

public class ActionExecutionKafkaListener {

    private static final Logger log = LoggerFactory.getLogger(ActionExecutionKafkaListener.class);
    private final AgentDispatcher dispatcher;

    public ActionExecutionKafkaListener(AgentDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @KafkaListener(
            topics = "${monify.kafka.agent-execution-topic}",
            groupId = "${monify.kafka.group-id}",
            containerFactory = "kafkaActionExecutionListenerContainerFactory"
    )
    public void handle(@Payload ActionExecutionRequestMessage requestMessage) {
        log.info("[Kafka] Received message from agent {}", requestMessage.correlationId());
        dispatcher.dispatch(requestMessage);
    }
}
