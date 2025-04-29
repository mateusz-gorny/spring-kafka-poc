package pl.monify.workflows.application;

import pl.monify.workflows.domain.StepRecord;
import pl.monify.workflows.domain.WorkflowInstance;
import pl.monify.workflows.domain.WorkflowStatus;
import pl.monify.workflows.events.WorkflowActionResponseEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DefaultWorkflowStateUpdater implements WorkflowStateUpdater {

    @Override
    public WorkflowInstance applyStep(WorkflowInstance instance, WorkflowActionResponseEvent event) {
        Map<String, Object> newContext = new HashMap<>(instance.context());
        newContext.putAll(event.output());

        List<StepRecord> newHistory = new ArrayList<>(instance.history());
        StepRecord step = new StepRecord(
                event.actionName(),
                Map.copyOf(instance.context()),
                Map.copyOf(event.output()),
                Instant.now(),
                WorkflowStatus.COMPLETED
        );
        newHistory.add(step);

        return new WorkflowInstance(
                instance.workflowInstanceId(),
                instance.definition(),
                instance.status(),
                newContext,
                List.copyOf(newHistory)
        );
    }
}
