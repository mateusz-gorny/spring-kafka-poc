package pl.monify.workflows.domain;

import java.time.Instant;
import java.util.Map;

public record StepRecord(
        String actionName,
        Map<String, Object> input,
        Map<String, Object> output,
        Instant timestamp,
        WorkflowStatus status
) {}
