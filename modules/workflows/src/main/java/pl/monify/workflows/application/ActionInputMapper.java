package pl.monify.workflows.application;

import pl.monify.workflows.domain.NextActionDefinition;

import java.util.Map;

public interface ActionInputMapper {
    Map<String, Object> map(NextActionDefinition nextDefinition,
                            Map<String, Object> context,
                            Map<String, Object> output);
}
