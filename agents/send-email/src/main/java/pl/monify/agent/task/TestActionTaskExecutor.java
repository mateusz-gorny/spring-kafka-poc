package pl.monify.agent.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.monify.agent.model.ActionExecutionRequestMessageModel;
import pl.monify.agent.model.ExecutorResultModel;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TestActionTaskExecutor implements ActionTaskExecutor {

    private static final Logger log = LoggerFactory.getLogger(TestActionTaskExecutor.class);
    private static final List<String> logs = new LinkedList<>();

    @Override
    public String getActionName() {
        return "test-action";
    }

    @Override
    public Map<String, Object> getInputSchema() {
        return Map.of("type", "object", "properties", Map.of(
                "Name", Map.of("type", "string"),
                "Number1", Map.of("type", "number"),
                "Number2", Map.of("type", "number"),
                "Number3", Map.of("type", "number")
        ));
    }

    @Override
    public Map<String, Object> getOutputSchema() {
        return Map.of("type", "object", "properties", Map.of(
                "result", Map.of("type", "number"),
                "status", Map.of("type", "string")
        ));
    }

    @Override
    public ExecutorResultModel execute(ActionExecutionRequestMessageModel actionExecutionRequestMessageModel) {
        int sum = 0;
        log.info("actionExecutionRequestMessageModel = {}", actionExecutionRequestMessageModel);
        Map<String, Object> input = actionExecutionRequestMessageModel.input();
        sum = Integer.parseInt((String) input.get("Number1")) + Integer.parseInt((String) input.get("Number2")) + Integer.parseInt((String) input.get("Number3"));

        log.info(logs.toString());
        return new ExecutorResultModel(
                "true",
                Map.of("result", sum, "status", "OK"),
                logs
        );
    }
}
