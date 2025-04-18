package pl.monify.actions.sendemail;

import java.util.Map;

public record ActionExecutionRequest(
        String actionInstanceId,
        Map<String, Object> input,
        String credentialId
) {}
