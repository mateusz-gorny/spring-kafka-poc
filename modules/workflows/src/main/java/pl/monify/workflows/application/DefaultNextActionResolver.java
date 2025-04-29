package pl.monify.workflows.application;

import pl.monify.workflows.domain.NextActionDefinition;
import pl.monify.workflows.domain.WorkflowDefinition;

import java.util.List;

public final class DefaultNextActionResolver implements NextActionResolver {
    @Override
    public List<NextActionDefinition> resolve(WorkflowDefinition definition, String lastActionName) {
        return definition.transitions()
                .getOrDefault(lastActionName, List.of());
    }
}
