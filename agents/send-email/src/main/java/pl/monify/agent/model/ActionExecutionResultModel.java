package pl.monify.agent.model;

public record ActionExecutionResultModel(String type, String correlationId, ExecutorResultModel payload) {
    public ActionExecutionResultModel(String correlationId, ExecutorResultModel payload) {
        this("ActionExecutionResult", correlationId, payload);
    }
}
