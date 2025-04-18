package pl.monify.workflows.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.monify.workflows.model.WorkflowInstance;
import pl.monify.workflows.repository.WorkflowInstanceRepository;

import java.util.List;

@RestController
@RequestMapping("/api/workflows")
public class WorkflowInstanceController {

    private final WorkflowInstanceRepository repository;

    public WorkflowInstanceController(WorkflowInstanceRepository repository) {
        this.repository = repository;
    }

    @PreAuthorize("hasAuthority('WORKFLOW_VIEW')")
    @GetMapping("/{workflowId}/instances")
    public List<WorkflowInstance> getInstancesForWorkflow(@PathVariable("workflowId") String workflowId) {
        return repository.findByWorkflowIdOrderByStartedAtDesc(workflowId);
    }

    @PreAuthorize("hasAuthority('WORKFLOW_VIEW')")
    @GetMapping("/instances/{instanceId}")
    public WorkflowInstance getInstanceDetails(@PathVariable("instanceId") String instanceId) {
        return repository.findById(instanceId)
                .orElseThrow(() -> new RuntimeException("Instance not found"));
    }
}
