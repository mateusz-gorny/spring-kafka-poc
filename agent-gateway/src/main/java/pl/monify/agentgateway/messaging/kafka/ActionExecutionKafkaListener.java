package pl.monify.agentgateway.messaging.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import pl.monify.agentgateway.messaging.ActionExecutionRequest;
import pl.monify.agentgateway.messaging.ActionExecutionRequestMessage;
import pl.monify.agentgateway.messaging.AgentDispatcher;

@Component
public class ActionExecutionKafkaListener {

    private static final Logger log = LoggerFactory.getLogger(ActionExecutionKafkaListener.class);
    private final AgentDispatcher dispatcher;

    public ActionExecutionKafkaListener(AgentDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @KafkaListener(
            topics = "action.execution.request",
            groupId = "agent.gateway.consumer.group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handle(@Payload ActionExecutionRequestMessage msg) {
        var request = new ActionExecutionRequest(
                "ActionExecutionRequest",
                msg.correlationId(),
                new ActionExecutionRequest.Payload(
                        msg.workflowInstanceId(),
                        msg.action(),
                        msg.input()
                )
        );

        boolean dispatched = dispatcher.dispatch(request, msg.action(), msg.teamId());
        if (!dispatched) {
            log.warn("No agent available for action={} team={}", msg.action(), msg.teamId());
        }
    }
}
