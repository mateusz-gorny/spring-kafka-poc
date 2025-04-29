package pl.monify.workflows.adapter.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import pl.monify.workflows.events.ActionExecutionRequestEvent;
import pl.monify.workflows.port.out.ActionExecutionRequestPublisher;

import java.util.Objects;

public class ActionExecutionRequestPublisherKafkaAdapter implements ActionExecutionRequestPublisher {
    private final KafkaTemplate<String, Object> kafka;

    public ActionExecutionRequestPublisherKafkaAdapter(
            KafkaTemplate<String, Object> kafka) {
        this.kafka = Objects.requireNonNull(kafka);
    }

    @Override
    public void publish(ActionExecutionRequestEvent event) {
        kafka.send("action.execution.request", event.workflowInstanceId(), event);
    }
}
