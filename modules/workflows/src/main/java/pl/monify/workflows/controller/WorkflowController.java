package pl.monify.workflows.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.monify.workflows.model.WorkflowDefinition;
import pl.monify.workflows.model.WorkflowInstance;
import pl.monify.workflows.repository.WorkflowInstanceRepository;
import pl.monify.workflows.service.WorkflowRunService;
import pl.monify.workflows.service.WorkflowService;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {

    private final WorkflowService service;
    private final WorkflowRunService runService;

    public WorkflowController(WorkflowService service, WorkflowRunService runService, WorkflowInstanceRepository repository) {
        this.service = service;
        this.runService = runService;
    }

    @PreAuthorize("hasAuthority('WORKFLOW_ADMIN')")
    @PostMapping
    public WorkflowDefinition create(@RequestBody WorkflowDefinition definition) {
        return service.create(definition);
    }

    @PreAuthorize("hasAuthority('WORKFLOW_VIEW')")
    @GetMapping
    public List<WorkflowDefinition> list(@RequestParam(name = "includeArchived", defaultValue = "false") boolean includeArchived) {
        return service.list(includeArchived);
    }

    @PreAuthorize("hasAuthority('WORKFLOW_VIEW')")
    @GetMapping("/{id}")
    public WorkflowDefinition get(@PathVariable("id") String id) {
        return service.getById(id)
                .orElseThrow(() -> new RuntimeException("Workflow not found"));
    }

    @PreAuthorize("hasAuthority('WORKFLOW_ADMIN')")
    @PutMapping("/{id}")
    public WorkflowDefinition update(@PathVariable("id") String id, @RequestBody WorkflowDefinition updated) {
        WorkflowDefinition existing = service.getById(id)
                .orElseThrow(() -> new RuntimeException("Workflow not found"));

        updated.setId(id);
        updated.setCreatedAt(existing.getCreatedAt());
        updated.setUpdatedAt(Instant.now());

        return service.update(updated);
    }

    @PreAuthorize("hasAuthority('WORKFLOW_ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        service.delete(id);
    }

    @PreAuthorize("hasAuthority('WORKFLOW_ADMIN')")
    @PostMapping("/{id}/run")
    public WorkflowInstance runAsync(@PathVariable("id") String id, @RequestBody Map<String, Object> payload) {
        return runService.runAsync(id, payload);
    }

    @PreAuthorize("hasAuthority('WORKFLOW_ADMIN')")
    @PostMapping("/{id}/run/sync")
    public WorkflowInstance runSync(@PathVariable("id") String id, @RequestBody Map<String, Object> payload) {
        return runService.runSync(id, payload);
    }
}
