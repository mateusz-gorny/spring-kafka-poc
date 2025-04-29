package pl.monify.workflows.api.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.monify.workflows.api.dto.WorkflowDefinitionRequest;
import pl.monify.workflows.api.dto.WorkflowDefinitionResponse;
import pl.monify.workflows.domain.WorkflowDefinition;
import pl.monify.workflows.domain.NextActionDefinition;
import pl.monify.workflows.port.out.WorkflowDefinitionRepository;
import pl.monify.workflows.port.out.WorkflowInstanceRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workflows/definitions")
public class WorkflowDefinitionController {

    private final WorkflowDefinitionRepository definitionRepository;
    private final WorkflowInstanceRepository instanceRepository;

    public WorkflowDefinitionController(WorkflowDefinitionRepository definitionRepository, WorkflowInstanceRepository instanceRepository) {
        this.definitionRepository = definitionRepository;
        this.instanceRepository = instanceRepository;
    }

    @PreAuthorize("hasAuthority('WORKFLOW_ADMIN')")
    @PostMapping
    public ResponseEntity<WorkflowDefinitionResponse> create(@RequestBody WorkflowDefinitionRequest request) {
        WorkflowDefinition definition = new WorkflowDefinition(null, UUID.randomUUID().toString(), toDomainTransitions(request.transitions()));
        WorkflowDefinition saved = definitionRepository.save(definition);
        return ResponseEntity.ok(toResponse(saved));
    }

    @PreAuthorize("hasAuthority('WORKFLOW_VIEW')")
    @GetMapping
    public ResponseEntity<List<WorkflowDefinitionResponse>> list() {
        List<WorkflowDefinition> definitions = definitionRepository.findAll();
        List<WorkflowDefinitionResponse> response = definitions.stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('WORKFLOW_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<WorkflowDefinitionResponse> get(@PathVariable("id") String id) {
        WorkflowDefinition definition = definitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workflow definition not found"));
        return ResponseEntity.ok(toResponse(definition));
    }

    @PreAuthorize("hasAuthority('WORKFLOW_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<WorkflowDefinitionResponse> update(@PathVariable("id") String id, @RequestBody WorkflowDefinitionRequest request) {
        WorkflowDefinition existing = definitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workflow definition not found: " + id));

        WorkflowDefinition updated = new WorkflowDefinition(
                existing.id(),
                UUID.randomUUID().toString(),
                toDomainTransitions(request.transitions())
        );
        WorkflowDefinition saved = definitionRepository.save(updated);
        return ResponseEntity.ok(toResponse(saved));
    }

    @PreAuthorize("hasAuthority('WORKFLOW_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        definitionRepository.delete(id);
        return ResponseEntity.noContent().build();
    }

    private WorkflowDefinitionResponse toResponse(WorkflowDefinition definition) {
        String status = instanceRepository.findAll().stream()
                .filter(i -> i.definition() != null && definition.id().equals(i.definition().id()))
                .anyMatch(i -> i.status() == pl.monify.workflows.domain.WorkflowStatus.RUNNING) ? "IN_PROGRESS" : "WAITING";

        Map<String, List<WorkflowDefinitionResponse.NextActionResponse>> transitions = definition.transitions().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .map(a -> new WorkflowDefinitionResponse.NextActionResponse(a.actionName(), a.outputToInputMapping()))
                                .toList()
                ));

        return new WorkflowDefinitionResponse(definition.id(), transitions, status);
    }

    private Map<String, List<NextActionDefinition>> toDomainTransitions(Map<String, List<WorkflowDefinitionRequest.NextActionRequest>> transitions) {
        return transitions.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .map(a -> new NextActionDefinition(a.actionName(), a.outputToInputMapping()))
                                .toList()
                ));
    }
}
