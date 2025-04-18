package pl.monify.workflows.service;

import org.springframework.stereotype.Service;
import pl.monify.workflows.model.WorkflowDefinition;
import pl.monify.workflows.model.WorkflowInstance;
import pl.monify.workflows.repository.WorkflowInstanceRepository;

import java.time.Instant;
import java.util.Map;

@Service
public class WorkflowRunService {

    private final WorkflowService workflowService;
    private final WorkflowInstanceRepository instanceRepository;

    public WorkflowRunService(WorkflowService workflowService, WorkflowInstanceRepository instanceRepository) {
        this.workflowService = workflowService;
        this.instanceRepository = instanceRepository;
    }

    public WorkflowInstance runAsync(String workflowId, Map<String, Object> payload) {
        WorkflowDefinition definition = workflowService.getById(workflowId)
                .orElseThrow(() -> new RuntimeException("Workflow not found"));

        WorkflowInstance instance = new WorkflowInstance();
        instance.setWorkflowId(definition.getId());
        instance.setStatus(WorkflowInstance.Status.QUEUED);
        instance.setTriggeredBy("api");
        instance.setPayload(payload);
        instance.setStartedAt(Instant.now());

        return instanceRepository.save(instance); // engine picks it up
    }

    public WorkflowInstance runSync(String workflowId, Map<String, Object> payload) {
        WorkflowInstance instance = runAsync(workflowId, payload);
        // In future: wait, poll, or use completable future
        return instance;
    }
}
