package pl.monify.workflows.adapter.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import pl.monify.workflows.events.WorkflowActionResponseEvent;
import pl.monify.workflows.port.in.WorkflowActionResponseHandler;

public class WorkflowActionResponseConsumer {
    private static final Logger log = LoggerFactory.getLogger(WorkflowActionResponseConsumer.class);
    private final WorkflowActionResponseHandler handler;

    public WorkflowActionResponseConsumer(WorkflowActionResponseHandler handler) {
        this.handler = handler;
    }

    @KafkaListener(
            topics = "${monify.kafka.workflow-action-response}",
            groupId = "${monify.kafka.group-id}",
            containerFactory = "kafkaWorkflowActionResponseEventListenerContainerFactory"
    )
    public void handle(ConsumerRecord<String, WorkflowActionResponseEvent> record) {
        log.info("Received action response: {}", record);
        try {
            handler.handle(record.value());
        } catch (Exception e) {
            log.error("Error handling action response", e);
        }
    }
}
