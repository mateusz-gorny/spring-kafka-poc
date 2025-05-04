package pl.monify.agentgateway.agentdelivery.domain.port.out;

import pl.monify.agentgateway.agentdelivery.domain.model.ActionExecutionRequestMessage;

public interface AgentSenderPort {
    void send(ActionExecutionRequestMessage request);
}
