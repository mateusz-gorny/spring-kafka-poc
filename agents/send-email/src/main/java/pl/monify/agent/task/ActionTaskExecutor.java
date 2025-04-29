package pl.monify.agent.task;

import pl.monify.agent.model.ActionExecutionRequestMessageModel;
import pl.monify.agent.model.ExecutorResultModel;

import java.util.Map;

public interface ActionTaskExecutor {

    String getActionName();
    Map<String, Object> getInputSchema();
    Map<String, Object> getOutputSchema();

    ExecutorResultModel execute(ActionExecutionRequestMessageModel actionExecutionRequestMessageModel);
}
