package pl.monify.workflows.domain;

import java.util.List;
import java.util.Map;

public record WorkflowDefinition(
        String id,
        String correlationId,
        Map<String, List<NextActionDefinition>> transitions
) {}
