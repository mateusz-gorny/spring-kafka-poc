package pl.monify.workflows.domain;

import java.util.List;
import java.util.Map;

public record WorkflowInstance(
        String workflowInstanceId,
        WorkflowDefinition definition,
        WorkflowStatus status,
        Map<String, Object> context,
        List<StepRecord> history
) {}
