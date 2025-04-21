package pl.monify.agentgateway.agentdelivery.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import pl.monify.agentgateway.agentdelivery.domain.application.AgentDispatcher;
import pl.monify.agentgateway.agentdelivery.domain.model.ActionExecutionRequestMessage;

import java.util.Map;

public class ActionExecutionKafkaListener {

    private final AgentDispatcher dispatcher;

    public ActionExecutionKafkaListener(AgentDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @KafkaListener(
            topics = "${monify.kafka.agent-execution-topic}",
            groupId = "agent.gateway.consumer.group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handle(@Payload Map<String, Object> message) {
        var request = new ActionExecutionRequestMessage(
                (String) message.get("workflowInstanceId"),
                (String) message.get("action"),
                (String) message.get("teamId"),
                (String) message.get("correlationId"),
                (Map<String, Object>) message.get("input")
        );
        dispatcher.dispatch(request);
    }
}
