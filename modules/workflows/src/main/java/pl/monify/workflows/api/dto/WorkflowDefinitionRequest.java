package pl.monify.workflows.api.dto;

import java.util.List;
import java.util.Map;

public record WorkflowDefinitionRequest(
        Map<String, List<NextActionRequest>> transitions
) {
    public record NextActionRequest(
            String actionName,
            Map<String, String> outputToInputMapping
    ) {}
}
