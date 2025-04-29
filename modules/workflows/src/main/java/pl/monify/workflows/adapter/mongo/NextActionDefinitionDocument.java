package pl.monify.workflows.adapter.mongo;

import java.util.Map;

public record NextActionDefinitionDocument(
        String actionName,
        Map<String, String> outputToInputMapping
) {}
