package pl.monify.agent.model;

public record RequestActionExecutionMessage(
        String type,
        String actionName,
        String ttl
) {
    public RequestActionExecutionMessage(String actionName, String ttl) {
        this("request-task", actionName, ttl);
    }
}
