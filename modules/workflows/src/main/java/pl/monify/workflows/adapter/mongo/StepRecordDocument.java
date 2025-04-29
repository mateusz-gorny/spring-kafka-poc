package pl.monify.workflows.adapter.mongo;

import java.time.Instant;
import java.util.Map;

public record StepRecordDocument(
        String actionName,
        Map<String, Object> input,
        Map<String, Object> output,
        Instant timestamp,
        String status
) {}
