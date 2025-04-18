package pl.monify.workflows.messaging;

import java.util.Map;

public record ActionExecutionRequest(
        String actionInstanceId,
        Map<String, Object> input,
        String credentialId
) {}
