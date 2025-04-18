package pl.monify.workflows.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.monify.workflows.messaging.ActionExecutionResult;
import pl.monify.workflows.model.WorkflowInstance;
import pl.monify.workflows.service.WorkflowInstanceService;

import java.util.Optional;

@Component
public class ActionResultListener {

    private static final Logger log = LoggerFactory.getLogger(ActionResultListener.class);
    private final WorkflowInstanceService instanceService;
    private final String TOPIC = "workflow.action.response";

    public ActionResultListener(WorkflowInstanceService instanceService) {
        this.instanceService = instanceService;
    }

    @KafkaListener(
            topics = TOPIC,
            groupId = "workflow.consumer.group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handle(ActionExecutionResult result) {
        log.info("Received action result {}", result);
        Optional<WorkflowInstance> instance = instanceService.findByActionInstanceId(result.actionInstanceId());

        if (instance.isEmpty()) {
            log.warn("No instance found for action {}", result.actionInstanceId());
            return;
        }

        log.info("Getting actions...");
        for (var action : instance.get().getActions()) {
            log.info("Action: {}", action);
            if (action.getActionId().equals(result.actionInstanceId())) {
                log.info("Setting result...");
                action.setStatus(result.success() ? "FINISHED" : "FAILED");
                action.setLog(result.log());
                action.setOutput(result.output());
            }
        }

        WorkflowInstance finalInstance = instance.get();

        boolean hasFailed = finalInstance.getActions().stream()
                .anyMatch(action -> action.getStatus().equals("FAILED"));

        boolean hasInProgress = finalInstance.getActions().stream()
                .anyMatch(action -> action.getStatus().equals("IN_PROGRESS"));

        boolean allFinished = finalInstance.getActions().stream()
                .allMatch(action -> action.getStatus().equals("FINISHED"));

        if (hasFailed) {
            finalInstance.setStatus(WorkflowInstance.Status.FAILED);
        } else if (hasInProgress) {
            finalInstance.setStatus(WorkflowInstance.Status.IN_PROGRESS);
        } else if (allFinished) {
            finalInstance.setStatus(WorkflowInstance.Status.FINISHED);
        } else {
            finalInstance.setStatus(WorkflowInstance.Status.QUEUED);
        }

        log.info("Saving instance: {}", instance);
        instanceService.save(finalInstance);
    }
}
