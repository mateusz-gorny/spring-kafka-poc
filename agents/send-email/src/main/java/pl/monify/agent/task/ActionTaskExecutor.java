package pl.monify.agent.task;

import pl.monify.agent.model.ActionExecutionRequestMessageModel;
import pl.monify.agent.model.ActionType;
import pl.monify.agent.model.ExecutorResultModel;

import java.time.Duration;
import java.util.Map;

public interface ActionTaskExecutor {

    String getActionName();

    default ActionType getActionType() {
        return ActionType.ACTION;
    }

    Map<String, Object> getInputSchema();
    Map<String, Object> getOutputSchema();

    ExecutorResultModel execute(ActionExecutionRequestMessageModel actionExecutionRequestMessageModel);

    Duration getTtl();
}
