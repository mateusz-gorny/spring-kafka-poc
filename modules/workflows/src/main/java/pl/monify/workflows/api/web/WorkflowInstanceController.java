package pl.monify.workflows.api.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.monify.workflows.api.dto.WorkflowInstanceResponse;
import pl.monify.workflows.api.dto.WorkflowStatusResponse;
import pl.monify.workflows.domain.StepRecord;
import pl.monify.workflows.domain.WorkflowInstance;
import pl.monify.workflows.port.out.WorkflowInstanceRepository;

import java.util.List;

@RestController
@RequestMapping("/api/workflows/instances")
public class WorkflowInstanceController {

    private final WorkflowInstanceRepository instanceRepository;

    public WorkflowInstanceController(WorkflowInstanceRepository instanceRepository) {
        this.instanceRepository = instanceRepository;
    }

    @PreAuthorize("hasAuthority('WORKFLOW_VIEW')")
    @GetMapping("/by/{definitionId}")
    public ResponseEntity<List<WorkflowInstanceResponse>> list(@PathVariable("definitionId") String definitionId) {
        return ResponseEntity.ok(instanceRepository.findByDefinitionId(definitionId).stream()
                .map(this::toResponse)
                .toList()
        );
    }

    @PreAuthorize("hasAuthority('WORKFLOW_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<WorkflowInstanceResponse> get(@PathVariable("id") String id) {
        WorkflowInstance instance = instanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workflow instance not found"));
        return ResponseEntity.ok(toResponse(instance));
    }

    @PreAuthorize("hasAuthority('WORKFLOW_VIEW')")
    @GetMapping("/{id}/status")
    public ResponseEntity<WorkflowStatusResponse> getStatus(@PathVariable("id") String id) {
        WorkflowInstance instance = instanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workflow instance not found"));
        return ResponseEntity.ok(new WorkflowStatusResponse(instance.status().name()));
    }

    private WorkflowInstanceResponse toResponse(WorkflowInstance instance) {
        List<WorkflowInstanceResponse.StepRecordResponse> history = instance.history().stream()
                .map(this::toStepRecordResponse)
                .toList();

        return new WorkflowInstanceResponse(
                instance.workflowInstanceId(),
                instance.definition().id(),
                instance.status().name(),
                instance.context(),
                history
        );
    }

    private WorkflowInstanceResponse.StepRecordResponse toStepRecordResponse(StepRecord record) {
        return new WorkflowInstanceResponse.StepRecordResponse(
                record.actionName(),
                record.input(),
                record.output(),
                record.timestamp().toString(),
                record.status().name()
        );
    }
}
