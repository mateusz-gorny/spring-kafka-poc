package pl.monify.workflows.api.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.monify.workflows.domain.WorkflowDefinition;
import pl.monify.workflows.events.ActionExecutionRequestEvent;
import pl.monify.workflows.port.out.ActionExecutionRequestPublisher;
import pl.monify.workflows.port.out.WorkflowDefinitionRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/workflows/execution")
public class WorkflowExecutionController {

    private final WorkflowDefinitionRepository definitionRepository;
    private final ActionExecutionRequestPublisher publisher;

    public WorkflowExecutionController(WorkflowDefinitionRepository definitionRepository, ActionExecutionRequestPublisher publisher) {
        this.definitionRepository = definitionRepository;
        this.publisher = publisher;
    }

    @PreAuthorize("hasAuthority('WORKFLOW_ADMIN')")
    @PostMapping("/{definitionId}/start")
    public ResponseEntity<Void> start(@PathVariable("definitionId") String definitionId) {
        WorkflowDefinition definition = definitionRepository.findById(definitionId)
                .orElseThrow(() -> new RuntimeException("Workflow definition not found"));

        var firstActions = definition.transitions().getOrDefault("start", List.of());

        for (var action : firstActions) {
            var correlationId = UUID.randomUUID().toString();
            definitionRepository.save(new WorkflowDefinition(
                    definitionId,
                    correlationId,
                    definition.transitions()
            ));

            Map<String, Object> inputs = new HashMap<>(action.outputToInputMapping());
            ActionExecutionRequestEvent event = new ActionExecutionRequestEvent(
                    definitionId,
                    null,
                    action.actionName(),
                    "team-abc",
                    correlationId,
                    inputs
            );
            publisher.publish(event);
        }

        return ResponseEntity.accepted().build();
    }
}
