package pl.monify.workflows.model;

import java.util.Map;

public record WorkflowActionDefinition(
        String id,
        String type,
        String name,
        String parallelGroup,
        Map<String, Object> input,
        String credentialId,
        String agentId
) {}
