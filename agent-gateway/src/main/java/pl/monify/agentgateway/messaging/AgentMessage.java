package pl.monify.agentgateway.messaging;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RegisterAgentMessage.class, name = "register"),
        @JsonSubTypes.Type(value = ActionExecutionResult.class, name = "ActionExecutionResult"),
        @JsonSubTypes.Type(value = PingMessage.class, name = "ping")
})
public interface AgentMessage {
    String type();
    String correlationId();
    Object payload();
}
