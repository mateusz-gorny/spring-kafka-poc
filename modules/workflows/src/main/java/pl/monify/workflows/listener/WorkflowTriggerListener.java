package pl.monify.workflows.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.monify.workflows.model.WorkflowInstance;
import pl.monify.workflows.service.WorkflowEngineService;
import pl.monify.workflows.service.WorkflowInstanceService;
import pl.monify.workflows.service.WorkflowService;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Component
public class WorkflowTriggerListener {

    private final WorkflowService workflowService;
    private final WorkflowInstanceService instanceService;
    private final WorkflowEngineService engineService;
    private static final String TOPIC = "workflow.trigger.event";

    public WorkflowTriggerListener(
            WorkflowService workflowService,
            WorkflowInstanceService instanceService,
            WorkflowEngineService engineService
    ) {
        this.workflowService = workflowService;
        this.instanceService = instanceService;
        this.engineService = engineService;
    }

    @KafkaListener(
            topics = TOPIC,
            groupId = "workflow.consumer.group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleTrigger(Map<String, Object> event) {
        if (!"workflow_trigger".equals(event.get("type"))) return;

        List<String> workflowIds = (List<String>) event.get("workflowIds");
        Map<String, Object> payload = (Map<String, Object>) event.get("payload");

        for (String workflowId : workflowIds) {
            workflowService.getById(workflowId).ifPresent(def -> {
                WorkflowInstance instance = new WorkflowInstance();
                instance.setWorkflowId(def.getId());
                instance.setStatus(WorkflowInstance.Status.QUEUED);
                instance.setTriggeredBy("trigger");
                instance.setPayload(payload);
                instance.setStartedAt(Instant.now());

                WorkflowInstance saved = instanceService.save(instance);
                engineService.execute(saved, def);
            });
        }
    }
}
