package pl.monify.workflows.api.dto;

import java.util.List;
import java.util.Map;

public record WorkflowDefinitionResponse(
        String id,
        Map<String, List<NextActionResponse>> transitions,
        String status
) {
    public record NextActionResponse(
            String actionName,
            Map<String, String> outputToInputMapping
    ) {}
}
