package pl.monify.workflows.domain;

import java.util.Map;

public record NextActionDefinition(
        String actionName,
        Map<String, String> outputToInputMapping  // key = input field name, value = SpEL expression
) {}
