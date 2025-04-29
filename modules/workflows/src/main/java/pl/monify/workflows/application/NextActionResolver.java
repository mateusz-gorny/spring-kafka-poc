package pl.monify.workflows.application;

import pl.monify.workflows.domain.NextActionDefinition;
import pl.monify.workflows.domain.WorkflowDefinition;

import java.util.List;

public interface NextActionResolver {
    List<NextActionDefinition> resolve(WorkflowDefinition definition, String lastActionName);
}
