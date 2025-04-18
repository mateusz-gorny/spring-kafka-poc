package pl.monify.workflows.messaging;

import java.util.Map;

public record ActionExecutionResult(
        String actionInstanceId,
        boolean success,
        String log,
        Map<String, Object> output
) {}
