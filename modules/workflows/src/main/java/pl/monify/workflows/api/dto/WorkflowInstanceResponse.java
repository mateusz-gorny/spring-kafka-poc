package pl.monify.workflows.api.dto;

import java.util.List;
import java.util.Map;

public record WorkflowInstanceResponse(
        String workflowInstanceId,
        String definitionId,
        String status,
        Map<String, Object> context,
        List<StepRecordResponse> history
) {
    public record StepRecordResponse(
            String actionName,
            Map<String, Object> input,
            Map<String, Object> output,
            String timestamp,
            String status
    ) {}
}
